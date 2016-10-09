/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

node {
    def mvnHome = tool "Maven"

    try {
        stage "Checkout"
        slackSend color: "#4272b7", message: "A new ${env.JOB_NAME} Pipeline build is starting... (Job ${env.BUILD_NUMBER})"
        checkoutFromGit()

        stage "Test"
        runTests(mvnHome)
        slackSend color: "good", message: "All tests for the ${env.JOB_NAME} Pipeline (Job ${env.BUILD_NUMBER}) have passed."

    } catch (err) {
        slackSend color: "danger", message: "An Error occurred during the ${env.JOB_NAME} Pipeline (Job ${env.BUILD_NUMBER})."
        error err
    }
}

def checkoutFromGit() {
    def branch = "master"
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