node {
    def mvnHome = tool "Maven";
    def agendaqaUrl = "https://agendaqa-aptitekk.rhcloud.com";

    try {
        stage "Checkout Stable";
        checkout([$class: "GitSCM", branches: [[name: "*/stable"]], browser: [$class: "GogsGit"], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: "542239bb-3d63-40bc-9cfa-e5ed56a1fc5b", url: "ssh://git@core.aptitekk.com:28/AptiTekk/Agenda.git"]]]);

        stage "Test Stable";
        sh "${mvnHome}/bin/mvn clean install -P wildfly-managed -U";
        slackSend color: "good", message: "All tests for the ${env.JOB_NAME} Pipeline (Job ${env.BUILD_NUMBER}) have passed. Deploying to QA...";

        stage "Build Stable WAR";
        sh "${mvnHome}/bin/mvn clean install -P openshift -U";

        stage "Deploy Stable WAR to QA";
        sh "rhc scp agendaqa upload deployments/ROOT.war wildfly/standalone/deployments/";

        def i = 0;

        while (i < 10) {
            sh "curl -o /dev/null --silent --head --write-out '%{http_code}' ${agendaqaUrl} > .agendaqa-status";
            def status = readFile ".agendaqa-status";

            if (status == "200") {
                break;
            }

            sleep 30;
            i++;
        }

        if (i == 10) {
            error "Could not connect to QA deployment after 5 minutes. Did it deploy?";
        }

        stage "Stable QA";
        slackSend color: "good", message: "A new QA build is ready for testing! Access it here: ${agendaqaUrl}";
        input "Please test https://agendaqa-aptitekk.rhcloud.com/ and proceed when ready.";
    } catch (org.jenkinsci.plugins.workflow.steps.FlowInterruptedException err) {
        for (cause in err.getCauses()) {
            if (cause instanceof org.jenkinsci.plugins.workflow.support.steps.StageStepExecution.CanceledCause) {
                slackSend color: "warning", message: "The ${env.JOB_NAME} Pipeline has been aborted by user. (Job ${env.BUILD_NUMBER})";
                return;
            }
        }
        slackSend color: "danger", message: "An Error occurred during the ${env.JOB_NAME} Pipeline (Job ${env.BUILD_NUMBER}). Error: ${err}";
    } catch (hudson.AbortException ignored) {
        slackSend color: "warning", message: "The ${env.JOB_NAME} Pipeline has been aborted by user. (Job ${env.BUILD_NUMBER})";
        return;
    } catch (err) {
        slackSend color: "danger", message: "An Error occurred during the ${env.JOB_NAME} Pipeline (Job ${env.BUILD_NUMBER}). Error: ${err}";
    }
}