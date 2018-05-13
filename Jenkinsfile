#!groovy

def app
node {
  //agent any
  //def app
  //stages {
    stage('Clone repo') {
      //steps {
        sh 'echo "Clone passed"'
      //}
  //    checkout scm
  //  sh 'echo "Clone"'
    }
    stage ('Build stage'){
      //steps {
        sh 'echo "Build passed"'
        app = docker.build("mohamedthings/hello")
    //      sh 'echo "Build"'
      //}
    }
    stage('Testing Stage') {
    //  steps {
        sh 'echo "OOOOOOOOOOOOOOOOOOO"'
        app.inside { sh 'echo "test inside IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII"'}

      //}
      //sh 'echo "Testing"'
      //app.inside{
      //}
    }
    stage('Push image') {

    //  steps {
        sh 'echo "Push passed"'
        sh 'ls'
        sh 'echo "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM"'
        print "Environment will be : ${env.NODE_ENV}"
        sh 'echo "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN"'
        //print "Commit : ${COMMIT}"
        //sh 'echo ${docker-hub-credentials}'
        print "Environment will be : ${env.BUILD_NUMBER}"
        sh 'echo "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN"'
      //}
      //sh 'echo "Push Image"'
      docker.withRegistry('https://registry.hub.docker.com','docker-hub-credentials'){
          app.push("${env.BUILD_NUMBER}")
          app.push("latest")
      }
      sh 'echo "ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ"'
    }

    stage('Deploy stage') {
  //  steps {
      sh 'echo "Deploy passed"'
      sh 'whoami'
      sh'sudo -H -u devops bash -c 'whoami''
      sh 'OOOOOOOOOOOOOOOOOOOOOo'
      //sh 'sudo kubectl run myphp2 --image=registry.hub.docker.com/mohamedthings/hello:40 --port=80'
      //sh 'sudo kubectl expose deployment myphp2 --type=LoadBalancer'
      //sh 'sudo minikube service myphp2 --url'
    //}
    }
  //}
  }
