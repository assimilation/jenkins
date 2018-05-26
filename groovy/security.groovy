#!groovy

//https://technologyconversations.com/2017/06/16/automating-jenkins-docker-setup/
 
import jenkins.model.*
import hudson.security.*
import jenkins.security.s2m.AdminWhitelistRule
import hudson.security.csrf.DefaultCrumbIssuer
 
def instance = Jenkins.getInstance()
 
def user = new File("/run/secrets/jenkins-user").text.trim()
def pass = new File("/run/secrets/jenkins-pass").text.trim()
 
def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount(user, pass)
instance.setSecurityRealm(hudsonRealm)
 
def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
instance.setAuthorizationStrategy(strategy)

//https://stackoverflow.com/questions/44501596/jenkins-disable-cli-over-remoting-via-a-groovy-script
instance.getDescriptor("jenkins.CLI").get().setEnabled(false)

//https://wiki.jenkins.io/display/JENKINS/CSRF+Protection
instance.setCrumbIssuer(new DefaultCrumbIssuer(true))

instance.save()
 
Jenkins.instance.getInjector().getInstance(AdminWhitelistRule.class).setMasterKillSwitch(false)
