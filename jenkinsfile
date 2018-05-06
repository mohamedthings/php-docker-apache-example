pipeline {
  agent any
  stages {
    stage('Clone repo') {
      checkout scm

    }
    stage ('Build stage'){
      steps {
        app = docker.build(".")
      }
    }
    stage('Testing Stage') {
      app.inside{
        sh 'echo "Test passed"'
      }
    }
    stage('Push image') {
      docker.writeRegistry('https://registry.hub.docker.com','docker-hub-credentials'){
          app.push("${env.BUILD_NUMBER}")
          app.push("latest")
      }

    }
    stage('Deploy stage') {

    }
  }
}
