def build() {
    return docker.build("npetersdev/spring-petclinic-rest")
}

def push(app) {
    docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
        //app.push("${env.BUILD_NUMBER}")
        app.push("latest")
    }
}

def run(container_name, port) {
    def remote = [:]
    remote.name = 'server'
    remote.host = '185.207.106.34'
    remote.port = 4714
    remote.allowAnyHosts = true
    withCredentials([sshUserPrivateKey(credentialsId: 'remote_guest_auth', keyFileVariable: 'KEYFILE', passphraseVariable: 'PASSPHRASE', usernameVariable: 'USERNAME')]) {
        remote.user = USERNAME
        remote.identityFile = KEYFILE
        remote.passphrase = PASSPHRASE

        try {
            sshCommand remote: remote, command: 'docker container stop ' + container_name
        } catch (err) {
            echo 'docker container not running'
        } finally {
            sshCommand remote: remote, command: 'docker pull npetersdev/spring-petclinic-rest:latest'
            sshCommand remote: remote, command: 'docker run --detach --rm --publish ' + port + ':9966 --name ' + container_name + ' npetersdev/spring-petclinic-rest:latest'
        }
    }
}

return this