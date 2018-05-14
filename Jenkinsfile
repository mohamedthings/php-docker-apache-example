#!groovy

def app
node {
    stage('Clone repo') {
        sh 'echo "Cloning"'
        checkout scm
        sh 'echo "Clone passed"'
    }
    stage ('Build stage'){
        sh 'echo "Building image"'
        app = docker.build("mohamedthings/hello")
        sh 'echo "Build passed"'
    }
    stage('Testing Stage') {
        app.inside { sh 'echo "container is alive "'}
    }
    stage('Push image') {
        sh 'echo "Pushing image"'
        print "Environment will be : ${env.NODE_ENV}"
        print "Build Number : ${env.BUILD_NUMBER}"
        docker.withRegistry('https://registry.hub.docker.com','docker-hub-credentials'){
          app.push("${env.BUILD_NUMBER}")
          //app.push("latest")
      }
      sh 'echo "image pushed to docker hub"'
    }

    stage('Deploy stage') {
      sh'echo "Deploing on minikube"'
    //  sh'echo "${env.BUILD_NUMBER}""'
      sh"sudo -H -u devops bash -c 'kubectl run myphp7 --image=registry.hub.docker.com/mohamedthings/hello:${env.BUILD_NUMBER} --port=80'"
      sh'sudo -H -u devops bash -c "kubectl expose deployment myphp7 --type=LoadBalancer"'
      sh'sudo -H -u devops bash -c "minikube service myphp7 --url"'
    }
  }
