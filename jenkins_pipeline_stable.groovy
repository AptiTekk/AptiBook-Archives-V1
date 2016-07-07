node {

    try {
        stage "Checkout";
        checkoutFromGit();

        stage "Test";
        runTests();
        slackSend color: "good", message: "All tests for the ${env.JOB_NAME} Pipeline (Job ${env.BUILD_NUMBER}) have passed. Deploying to QA...";

        stage "Build";
        buildStableWAR();

        stage "Deploy to QA";
        deployToQA();

        stage "QA Verification";
        getQAInput();

    } catch (hudson.AbortException ignored) {
        if (!Global.abortHandled) {
            slackSend color: "warning", message: "The ${env.JOB_NAME} Pipeline has been aborted by user. (Job ${env.BUILD_NUMBER})";
            error "Aborted by User.";
        }
    } catch (err) {
        if (!Global.abortHandled) {
            slackSend color: "danger", message: "An Error occurred during the ${env.JOB_NAME} Pipeline (Job ${env.BUILD_NUMBER}). Error: ${err}";
            error err.message;
        }
    }
}

class Global {
    String qaGearName = "agendaqa";
    GString qaUrl = "https://${qaGearName}-aptitekk.rhcloud.com";

    def mvnHome = tool "Maven";
    boolean abortHandled = false;
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

def runTests() {
    sh "${Global.mvnHome}/bin/mvn clean install -P wildfly-managed -U";
}

def buildStableWAR() {
    sh "${Global.mvnHome}/bin/mvn clean install -P openshift -U";
}

def deployToQA() {
    sh "rhc scp ${Global.qaGearName} upload deployments/ROOT.war wildfly/standalone/deployments/";

    def i = 0;

    while (i < 10) {
        sh "curl -o /dev/null --silent --head --write-out '%{http_code}' ${Global.qaUrl} > .${env.JOB_NAME}${env.BUILD_NUMBER}qa-status";
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

def getQAInput() {
    try {
        slackSend color: "good", message: "A new QA build is ready for testing! Access it here: ${Global.qaUrl}";
        input message: "Please test ${Global.qaUrl} and proceed when ready.", ok: "Proceed";
    } catch (org.jenkinsci.plugins.workflow.steps.FlowInterruptedException ignored) {
        slackSend color: "warning", message: "The ${env.JOB_NAME} Pipeline has been aborted by user. (Job ${env.BUILD_NUMBER})";
        Global.abortHandled = true;
        error "Aborted by User.";
    }
}
