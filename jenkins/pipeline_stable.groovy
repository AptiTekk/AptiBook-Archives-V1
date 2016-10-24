/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

node {
    def herokuAppName = "aptitekk-aptibook"
    def jenkinsUrl = "https://dev.aptitekk.com/jenkins/"
    def liveUrl = "https://aptibook.aptitekk.com/"
    def pingUrl = "https://aptibook.aptitekk.com/ping"
    def mvnHome = tool "Maven"

    try {
        stage "Checkout"
        slackSend color: "#4272b7", message: "[Job ${env.BUILD_NUMBER}] A new ${env.JOB_NAME} Pipeline build is starting..."
        checkoutFromGit()

        stage "Test"
        runTests(mvnHome)
        slackSend color: "good", message: "[Job ${env.BUILD_NUMBER}] All tests for the ${env.JOB_NAME} Pipeline have passed. Ready to deploy to Production."

        changeVersion(mvnHome, env.BUILD_NUMBER)

        stage "Deploy Approval"
        if (!getDeploymentApproval(jenkinsUrl)) {
            echo "Aborted by User."
            slackSend color: "warning", message: "[Job ${env.BUILD_NUMBER}] The ${env.JOB_NAME} Pipeline has been aborted by the user."
            return
        }

        stage "Deploy to Production"
        slackSend color: "#4272b7", message: "[Job ${env.BUILD_NUMBER}] Deploying AptiBook to Heroku..."
        deployToProduction(mvnHome, herokuAppName, liveUrl, pingUrl)

    } catch (err) {
        slackSend color: "danger", message: "[Job ${env.BUILD_NUMBER}] An Error occurred during the ${env.JOB_NAME} Pipeline."
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

def changeVersion(mvnHome, buildNumber) {
    sh mvnHome + '/bin/mvn versions:set -DnewVersion="`date +%Y.%m.%d`_' + buildNumber + '"'
    sh mvnHome + '/bin/mvn versions:commit'
    sh "git commit -a -m 'Jenkins Automatic Version Change'"
}

def deployToProduction(mvnHome, herokuAppName, liveUrl, pingUrl) {
    sh "${mvnHome}/bin/mvn clean install -U"
    sh "heroku maintenance:on --app ${herokuAppName}"
    sh "heroku git:remote --app ${herokuAppName}"
    sh "git push heroku HEAD:master -f"
    sleep 60

    sh mvnHome + '/bin/mvn help:evaluate -Dexpression=project.version|grep -Ev \'(^\\[|Download\\w+:)\' > currentVersion'
    def version = readFile "currentVersion"

    slackSend color: "good", message: "[Job ${env.BUILD_NUMBER}] ${herokuAppName} version ${version} has been deployed."
    slackSend color: "#4272b7", message: "[Job ${env.BUILD_NUMBER}] Please disable Maintenance Mode when ready."
}

boolean getDeploymentApproval(jenkinsUrl) {
    try {
        slackSend color: "#4272b7", message: "[Job ${env.BUILD_NUMBER}] Please approve or abort the pending deployment at ${jenkinsUrl}."
        input message: "Please approve when you are ready to deploy.", ok: "Approve"
        return true
    } catch (ignored) {
        return false
    }
}