/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

node {
    def herokuAppName = "aptitekk-aptibook"
    def liveUrl = "https://aptibook.aptitekk.com/"
    def pingUrl = "https://aptibook.aptitekk.com/ping"
    def mvnHome = tool "Maven"

    try {
        stage "Checkout"
        slackSend color: "#4272b7", message: "A new AptiBook build is starting... (Job ${env.BUILD_NUMBER})"
        checkoutFromGit()

        stage "Test"
        runTests(mvnHome)
        slackSend color: "good", message: "All tests for the ${env.JOB_NAME} Pipeline (Job ${env.BUILD_NUMBER}) have passed. Ready to deploy to Production."

        stage "Deploy Approval"
        if (!getDeploymentApproval()) {
            echo "Aborted by User."
            slackSend color: "warning", message: "The ${env.JOB_NAME} Pipeline has been aborted by the user. (Job ${env.BUILD_NUMBER})"
            return
        }

        stage "Deploy to Production"
        slackSend color: "#4272b7", message: "Deploying AptiBook to Heroku... (Job ${env.BUILD_NUMBER})"
        deployToProduction(mvnHome, herokuAppName, liveUrl, pingUrl)

    } catch (err) {
        slackSend color: "danger", message: "An Error occurred during the ${env.JOB_NAME} Pipeline (Job ${env.BUILD_NUMBER})."
        error err
    }
}

def checkoutFromGit() {
    def branch = "stable"
    def url = "ssh://git@util.aptitekk.com:2005/ab/aptibook.git"
    def credentialsId = "542239bb-3d63-40bc-9cfa-e5ed56a1fc5b"

    checkout([$class                           : "GitSCM",
              branches                         : [[name: "*/${branch}"]],
              browser                          : [$class: 'BitbucketWeb', repoUrl: 'https://dev.aptitekk.com/git/projects/AB/repos/aptibook/browse'],
              doGenerateSubmoduleConfigurations: false,
              extensions                       : [],
              submoduleCfg                     : [],
              userRemoteConfigs                : [[credentialsId: "${credentialsId}", url: "${url}"]]
    ])
}

def runTests(mvnHome) {
    sh "${mvnHome}/bin/mvn clean install -P test -U"
}

def deployToProduction(mvnHome, herokuAppName, liveUrl, pingUrl) {
    sh "${mvnHome}/bin/mvn clean install -U"
    sh "heroku maintenance:on --app ${herokuAppName}"
    sh "heroku git:remote --app ${herokuAppName}"
    sh "git push heroku HEAD:master"
    sleep 60
    sh "heroku maintenance:off --app ${herokuAppName}"

    def i = 0;
    def found = false;

    while (i < 60) {
        sh "curl --silent ${pingUrl} > ping"
        def response = readFile "ping"

        if (response == "pong") {
            found = true;
            break
        }

        sleep 3
        i++
    }

    if (found)
        slackSend color: "good", message: "A new deployment of ${herokuAppName} has been deployed successfully at ${liveUrl}."
    else {
        slackSend color: "danger", message: "Deployment of ${herokuAppName} was not detected after 3 minutes. Did it deploy?"
        error
    }
}

def boolean getDeploymentApproval() {
    try {
        slackSend color: "#4272b7", message: "Please approve or abort the pending deployment."
        input message: "Please approve when you are ready to deploy.", ok: "Approve"
        return true
    } catch (ignored) {
        return false
    }
}