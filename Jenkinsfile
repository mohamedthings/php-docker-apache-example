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
        app = docker.build("myphpproject55/hello")
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

      //}
      //sh 'echo "Push Image"'
      //docker.writeRegistry('https://registry.hub.docker.com','docker-hub-credentials'){
          //app.push("${env.BUILD_NUMBER}")
        //  app.push("latest")
      //}
    }

    stage('Deploy stage') {
  //  steps {
      sh 'echo "Deploy passed"'

    //}
    }
  //}
  }
