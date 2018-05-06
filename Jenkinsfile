pipeline {
  agent any
//  def app
  stages {
    stage('Clone repo') {
      steps {

      }
  //    checkout scm
  //  sh 'echo "Clone"'
    }
    stage ('Build stage'){
      steps {
        //app = docker.build(".")
    //      sh 'echo "Build"'
      }
    }
    stage('Testing Stage') {
      steps {

      }
      //sh 'echo "Testing"'
      //app.inside{
        //sh 'echo "Test passed"'
      //}
    }
    stage('Push image') {

      steps {

      }
      //sh 'echo "Push Image"'
      //docker.writeRegistry('https://registry.hub.docker.com','docker-hub-credentials'){
        //  app.push("${env.BUILD_NUMBER}")
          //app.push("latest")
      }

    stage('Deploy stage') {
    //  sh 'echo "Deploy"'
    steps {

    }
    }
  }
  }
