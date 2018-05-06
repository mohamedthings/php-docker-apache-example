def app
//def imageName = 'gcr.io/hungerstation-configs/delivery-portal'
//def deployableBranches = ["staging", "master"]
//def channels = ["hs-logistics"]
//def serviceName = 'delivery-portal'

node {
  //ansiColor('xterm') {
    stage('Clone repository') {
      sh 'ls'
    }

    stage('Build image') {
      app = docker.build("myphpproject",".")

    }

    stage('Push image') {
      sh 'echo "push"'

    }

    stage('Deploy') {
      sh 'echo "Deploy"'

    }
//  }
}
