pipeline {
    agent any
    stages {
        stage('Test') {
            when {
                branch 'feature/pipeline'
            }
            agent {
                dockerfile {
                    filename 'Dockerfile.test'
                    args '-v $HOME/.m2:/root/.m2'
                }
            }
            // steps {
            //     sh 'mvn test'
            // }
        }
        stage('Build & Push docker image') {
            when {
                branch 'master'
            }
            steps {
                script {
                    def prod = load "jobs/production.groovy"
                    def app = prod.build()
                    prod.push(app)
                }
            }
        }
        stage('Run docker image on remote server A') {
            when {
                branch 'master'
            }
            steps {
                script {
                    def prod = load "jobs/production.groovy"
                    prod.run('spring-petclinic-rest-A', 9966)
                }
            }
        }
        stage ('Wait') {
            steps {
                echo 'Waiting for container A to start up'
                sleep 30 // seconds
            }
        }
        stage('Run docker image on remote server B') {
            when {
                branch 'master'
            }
            steps {
                script {
                    def prod = load "jobs/production.groovy"
                    prod.run('spring-petclinic-rest-B', 9977)
                }
            }
        }
        stage('Delete unused docker image') {
            when {
                branch 'master'
            }
            steps {
                sh 'docker rmi npetersdev/spring-petclinic-rest:latest'
            }
        }
    }
}