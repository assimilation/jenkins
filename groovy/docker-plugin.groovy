#!groovy

//https://gist.github.com/adrianlzt/d092b5852600b19c08d2d704c8633e09

/*
 * Automatically configure the docker cloud in Jenkins.
 * Tested with:
 *   - {name: 'docker-plugin' ver: '1.1.2'}
 *
 * Based on: https://gist.github.com/stuart-warren/e458c8439bcddb975c96b96bec3971b6
*/
import jenkins.model.*;
import hudson.model.*;
import com.nirima.jenkins.plugins.docker.DockerCloud
import com.nirima.jenkins.plugins.docker.DockerTemplate
import com.nirima.jenkins.plugins.docker.DockerTemplateBase
import io.jenkins.docker.connector.DockerComputerAttachConnector
import org.jenkinsci.plugins.docker.commons.credentials.DockerServerEndpoint

// https://github.com/jenkinsci/docker-plugin/blob/docker-plugin-1.1.2/src/main/java/com/nirima/jenkins/plugins/docker/DockerTemplateBase.java#L122
DockerTemplateBase templateBase = new DockerTemplateBase(
      						  "jenkins/jnlp-slave", // image
                              null, // pullCredentialsId
                              null, // dnsString
                              null, // network
                              null, // dockerCommand
                              """/var/run/docker.sock:/var/run/docker.sock""", // volumesString
                              null, // volumesFromString
                              null, // environmentsString
                              null, // hostname
                              null, // memoryLimit
                              null, // memorySwap
                              null, // cpuShares
                              null, // bindPorts
                              false, // bindAllPorts
                              false, // privileged
                              false, // tty
                              null, // macAddress
                              "" // extraHostsString
);

DockerComputerAttachConnector connector = new DockerComputerAttachConnector("root")

DockerTemplate dkTemplate = new DockerTemplate(templateBase,connector,"docker","","");

ArrayList<DockerTemplate> dkTemplates = new ArrayList<DockerTemplate>();
dkTemplates.add(dkTemplate);

def smu = System.getenv('SWARM_MASTER_URL')
def dse = "tcp://" + smu + ":2376"

DockerServerEndpoint endpoint = new DockerServerEndpoint(dse, "")

ArrayList<DockerCloud> dkCloud = new ArrayList<DockerCloud>();
dkCloud.add(
		// https://github.com/jenkinsci/docker-plugin/blob/docker-plugin-1.1.2/src/main/java/com/nirima/jenkins/plugins/docker/DockerCloud.java#L106
		new DockerCloud(
				"docker",
				dkTemplates,
				endpoint,
				100,
				60,
				60,
				"",
				""
			)
);

Jenkins.getInstance().clouds.replaceBy(dkCloud)
