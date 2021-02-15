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
    stage('Run docker image on remote server A') {
      steps {
        script {
          withCredentials([sshUserPrivateKey(credentialsId: 'remote_guest_auth', keyFileVariable: 'KEYFILE', passphraseVariable: 'PASSPHRASE', usernameVariable: 'USERNAME')]) {
            def remote = [:]
            remote.name = 'server'
            remote.host = '185.207.106.34'
            remote.port = 4714
            remote.allowAnyHosts = true
            remote.user = USERNAME
            remote.identityFile = KEYFILE
            remote.passphrase = PASSPHRASE
            
            try {
              sshCommand remote: remote, command: 'docker container stop spring-petclinic-rest-A'
            } catch (err) {
              echo 'docker container not running'
            } finally {
              sshCommand remote: remote, command: 'docker pull npetersdev/spring-petclinic-rest:latest'
              sshCommand remote: remote, command: 'docker run --detach --rm --publish 9966:9966 --name spring-petclinic-rest-A npetersdev/spring-petclinic-rest:latest'
            }
          }
        }
      }
    }
    stage('Run docker image on remote server B') {
      steps {
        script {
          withCredentials([sshUserPrivateKey(credentialsId: 'remote_guest_auth', keyFileVariable: 'KEYFILE', passphraseVariable: 'PASSPHRASE', usernameVariable: 'USERNAME')]) {
            def remote = [:]
            remote.name = 'server'
            remote.host = '185.207.106.34'
            remote.port = 4714
            remote.allowAnyHosts = true
            remote.user = USERNAME
            remote.identityFile = KEYFILE
            remote.passphrase = PASSPHRASE

            try {
              sshCommand remote: remote, command: 'docker container stop spring-petclinic-rest-B'
            } catch (err) {
              echo 'docker container not running'
            } finally {
              sshCommand remote: remote, command: 'docker pull npetersdev/spring-petclinic-rest:latest'
              sshCommand remote: remote, command: 'docker run --detach --rm --publish 9977:9966 --name spring-petclinic-rest-B npetersdev/spring-petclinic-rest:latest'
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
  }
}
