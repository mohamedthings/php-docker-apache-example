import hudson.model.Result
import jenkins.model.CauseOfInterruption.UserInterruption

def killOldBuilds() {
  while(currentBuild.rawBuild.getPreviousBuildInProgress() != null) {
    currentBuild.rawBuild.getPreviousBuildInProgress().doKill()
  }
}

def setup() {
  killOldBuilds()
  stash name: "${getCommit()}", includes: ""
}

def matcher(text, regex) {
  def matcher = text =~ regex
  matcher ? matcher[0][1] : null
}

def getVersion() {
  return matcher(sh (script: "cat VERSION.json", returnStdout: true), '"version":\\s*"(.+)"')
}

def getCommit(def step=null) {
  if (step == null) {
    step = 0
  }

  return sh (script: "git rev-parse --short HEAD~${step}", returnStdout: true).replaceAll('\n', '')
}

def releaseTag() {
  return sh(script: "git for-each-ref --sort=taggerdate --format '%(tag)' | tail -1", returnStdout: true).trim()
}

def releaseChangelog() {
  currentTag = releaseTag()
  previousTag = sh(script: "git for-each-ref --sort=taggerdate --format '%(tag)' | tail -2 | head -1", returnStdout: true).trim()

  return sh(script: "git shortlog -w0 -n --no-merges --perl-regexp --author='^((?!ms-hs-bot).*)\$' ${previousTag}..${currentTag}", returnStdout: true).trim()
}

def releaseURL() {
  return "https://github.com/deliveryhero/${getRepoName()}/releases/${releaseTag()}"
}

def dockerRegistry(closure) {
    docker.withRegistry('https://gcr.io', 'gcr:google-container-access'){
        closure()
    }
}

def notifySlack(channels, message) {
  for(channel in channels) {
    slackSend channel: channel, teamDomain: 'hungerstation-dev', color: '#00CC00', message: message, failOnError: false
  }
}

def pushImage(app, tags) {
  dockerRegistry {
    for(tag in tags) app.push(tag)
    if(BRANCH_NAME == 'master') app.push("latest")
  }
}

def sanitize(string) {
  return string.replaceAll('/', '-').toLowerCase()
}

def updateSecrets(secretName, secretPath) {
  BRANCH_NAME = sanitize(BRANCH_NAME)
  if(BRANCH_NAME == "staging") {
    credentialsId = '9efba9f9-bf0d-4a42-a122-c489eb3a40ba'
    serverUrl = 'https://35.184.210.247'
    secretsFileName = 'secrets-staging.yml'
  }
  if(BRANCH_NAME == "master") {
    credentialsId = '115bb3ae-c5f1-451f-bff4-b1a22c72c662'
    serverUrl = 'https://35.205.43.105'
    secretsFileName = 'secrets-production.yml'
  }
  wrap([$class: 'KubectlBuildWrapper', credentialsId: "$credentialsId", serverUrl: "$serverUrl"]) {
    sh "kubectl --namespace=default delete secrets $secretName || true"
    sh "kubectl --namespace=default create secret generic $secretName --from-file=secrets.yml=$secretPath/$secretsFileName"
  }
}

def updateJob(jobName, jobPath, IMAGE_TAG) {
  BRANCH_NAME = sanitize(BRANCH_NAME)
  if(BRANCH_NAME == "staging") {
    credentialsId = '9efba9f9-bf0d-4a42-a122-c489eb3a40ba'
    serverUrl = 'https://35.184.210.247'
    if (IMAGE_TAG) {
      NEWTAG = IMAGE_TAG
    } else{
      NEWTAG = "$BRANCH_NAME-$BUILD_NUMBER"
    }
  }
  if(BRANCH_NAME == "master") {
    credentialsId = '115bb3ae-c5f1-451f-bff4-b1a22c72c662'
    serverUrl = 'https://35.205.43.105'
    if (IMAGE_TAG) {
      NEWTAG = IMAGE_TAG
    } else{
      NEWTAG = "$BRANCH_NAME-${getVersion()}"
    }
  }
  wrap([$class: 'KubectlBuildWrapper', credentialsId: "$credentialsId", serverUrl: "$serverUrl"]) {
    sh "sed 's/{{TAG}}/$NEWTAG/g' $jobPath > migration.yaml"
    sh "kubectl --namespace=default delete job $jobName || true"
    sh "kubectl --namespace=default create -f migration.yaml"
  }
}

def deployKubernetes(deploymentName, containerName, imageName, IMAGE_TAG) {
  BRANCH_NAME = sanitize(BRANCH_NAME)
  if(BRANCH_NAME == "staging") {
    credentialsId = '9efba9f9-bf0d-4a42-a122-c489eb3a40ba'
    serverUrl = 'https://35.184.210.247'
    if (IMAGE_TAG) {
      TAG = IMAGE_TAG
    } else{
      TAG = "$BRANCH_NAME-$BUILD_NUMBER"
    }
  }
  if(BRANCH_NAME == "master") {
    credentialsId = '115bb3ae-c5f1-451f-bff4-b1a22c72c662'
    serverUrl = 'https://35.205.43.105'
    if (IMAGE_TAG) {
      TAG = IMAGE_TAG
    } else{
      TAG = "$BRANCH_NAME-${getVersion()}"
    }
  }
  wrap([$class: 'KubectlBuildWrapper', credentialsId: "$credentialsId", serverUrl: "$serverUrl"]) {
    sh "kubectl --namespace=default set image deployment $deploymentName $containerName=$imageName:$TAG --record"
  }
}

def preparTest(app, command, closure) {
  dockerRegistry {
    app.pull() // pull test image
    sh "$command" // command for tests init (a.k.a infra-config/ci/prep-test-env.sh)
  }
  app.inside("-e 'RAILS_ENV=test' -e 'RACK_ENV=test' --network isolated_nw") {
    closure()
  }
}

def getRepoName() {
  return matcher(sh (script: "git remote show -n origin | grep Fetch", returnStdout: true), '/([^/]*).git$')
}

def addRevisionToImage() {
  withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'github-access', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
    sh "git rev-parse --short HEAD | tee REVISION"
  }
}

def bumpVersion() {
  retVal = false
  withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'github-access', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
    // Set https origin
    origin = "https://${URLEncoder.encode(USERNAME, "UTF-8")}:${URLEncoder.encode(PASSWORD, "UTF-8")}@github.com/deliveryhero/${getRepoName()}.git"

    // Sync branch to get refs
    sh "git fetch $origin $BRANCH_NAME"
    sh "git checkout $BRANCH_NAME"

    // Get last commit message
    commitMsg = sh(script: 'git log -1 --pretty=%B $(git rev-parse HEAD)', returnStdout: true).trim()

    // If it's a version message stop and return that it was already bumped
    if (commitMsg =~ /^Version [0-9.]+$/) {
      println 'Commit is version commit, building'
      retVal = true
      return true
    }
  }
  return retVal
}

return this
