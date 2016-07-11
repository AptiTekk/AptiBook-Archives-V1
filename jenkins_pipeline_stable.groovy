node {
    def qaGearName = "agendaqa";
    def qaUrl = "https://" + qaGearName + "-aptitekk.rhcloud.com";

    def mvnHome = tool "Maven";

    try {
        stage "Checkout";
        checkoutFromGit();

        stage "Test";
        runTests(mvnHome);
        slackSend color: "good", message: "All tests for the ${env.JOB_NAME} Pipeline (Job ${env.BUILD_NUMBER}) have passed. Deploying to QA...";

        stage "Build";
        buildStableWAR(mvnHome);

        stage "Deploy to QA";
        deployToQA(qaGearName, qaUrl);

        stage "QA Verification";
        if (!getQAInput(qaUrl)) {
            echo "Aborted by User.";
            slackSend color: "warning", message: "The ${env.JOB_NAME} Pipeline has been aborted by user. (Job ${env.BUILD_NUMBER})";
            return;
        }
    } catch (err) {
        slackSend color: "danger", message: "An Error occurred during the ${env.JOB_NAME} Pipeline (Job ${env.BUILD_NUMBER}). Error: ${err}";
        error err;
    }
}

def checkoutFromGit() {
    def branch = "stable";
    def url = "ssh://git@core.aptitekk.com:28/AptiTekk/Agenda.git";
    def credentialsId = "542239bb-3d63-40bc-9cfa-e5ed56a1fc5b";

    checkout([$class                           : "GitSCM",
              branches                         : [[name: "*/${branch}"]],
              browser                          : [$class: "GogsGit"],
              doGenerateSubmoduleConfigurations: false,
              extensions                       : [],
              submoduleCfg                     : [],
              userRemoteConfigs                : [[
                                                          credentialsId: "${credentialsId}",
                                                          url          : "${url}"
                                                  ]]
    ]);
}

def runTests(mvnHome) {
    sh "${mvnHome}/bin/mvn clean install -P wildfly-managed -U";
}

def buildStableWAR(mvnHome) {
    sh "${mvnHome}/bin/mvn clean install -P openshift -U";
}

def deployToQA(qaGearName, qaUrl) {
    sh "rhc app stop ${qaGearName}";
    sh "rhc scp ${qaGearName} upload deployments/ROOT.war wildfly/standalone/deployments/";
    sh "rhc ssh ${qaGearName} \"rm -irf wildfly/welcome-content\"";
    sh "rhc app start ${qaGearName}";

    def i = 0;

    while (i < 10) {
        sh "curl -o /dev/null --silent --head --write-out '%{http_code}' ${qaUrl} > .${env.JOB_NAME}${env.BUILD_NUMBER}qa-status";
        def status = readFile ".${env.JOB_NAME}${env.BUILD_NUMBER}qa-status";

        if (status == "200") {
            break;
        }

        sleep 30;
        i++;
    }

    if (i == 10) {
        error "Could not connect to QA deployment after 5 minutes. Did it deploy?";
    }
}

def boolean getQAInput(qaUrl) {
    try {
        slackSend color: "good", message: "A new QA build is ready for testing! Access it here: ${qaUrl}";
        input message: "Please test ${qaUrl} and proceed when ready.", ok: "Proceed";
        return true;
    } catch (ignored) {
        return false;
    }
}