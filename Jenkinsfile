pipeline {
  agent any
  stages {
    stage('Build & push docker image') {
      steps {
        script {
          def app = docker.build("npetersdev/spring-petclinic-rest")
          docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
            //app.push("${env.BUILD_NUMBER}")
            app.push("latest")
          }
        }

      }
    }

    stage('Run docker image on remote server') {
      steps {
        script {
          withCredentials([usernamePassword(credentialsId: 'remote-server-credentials', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
            def remote = [:]
            remote.name = 'server'
            remote.host = 'server.ninopeters.de'
            remote.user = USERNAME
            remote.password = PASSWORD
            remote.allowAnyHosts = true

            try {
              sshCommand remote: remote, command: 'docker container stop spring-petclinic-rest'
            } catch (err) {
              echo 'docker container not running'
            } finally {
              sshCommand remote: remote, command: 'docker pull npetersdev/spring-petclinic-rest:latest'
              sshCommand remote: remote, command: 'docker run --detach --rm --publish 9966:9966 --name spring-petclinic-rest npetersdev/spring-petclinic-rest:latest'
            }
          }
        }

      }
    }

    stage('Delete unused docker image') {
      steps {
        sh 'docker rmi npetersdev/spring-petclinic-rest:latest'
      }
    }

    stage('Postman') {
      steps {
        sh 'newman run https://api.getpostman.com/collections/14312820-c39aca89-b267-4d97-a2ca-b65df579f9fa?apikey=PMAK-60101515c9205f003495db6d-37971a23f29ae9913c6657a8fe028239f5 --environment https://api.getpostman.com/environments/14312820-58506620-e644-46ff-a263-1884e7935177?apikey=PMAK-60101515c9205f003495db6d-37971a23f29ae9913c6657a8fe028239f5'
      }
    }

  }
}