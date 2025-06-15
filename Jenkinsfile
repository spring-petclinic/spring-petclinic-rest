pipeline {
  agent { label 'Agent1' }

  environment {
    TF_VAR_project_id = 'earnest-scene-454409-t5'
  }

  stages {
    stage('Clone Repo') {
      steps {
        script {
          if (fileExists('spring-petclinic-rest')) {
            sh '''cd spring-petclinic-rest 
            git pull'''
          } else {
            sh 'git clone https://github.com/KahanHM/spring-petclinic-rest.git'
          }
        }
      }
    }

     stage('Check Terraform Changes') {
      steps {
        script {
          // Check if any *.tf files changed in repo
          def changes = sh(
            script: "git -C GCE diff --name-only HEAD~1 HEAD | grep '.tf' || true",
            returnStdout: true
          ).trim()
          env.TF_CHANGED = changes ? 'true' : 'false'
          echo "Terraform files changed? ${env.TF_CHANGED}"
        }
      }
    }

    stage('Security and Validation Checks') {
      when { expression { env.TF_CHANGED == 'true' } }
      steps {
        script {
          // Run tfsec
          try {
            sh 'docker run --rm -v "$PWD/GCE:/src" aquasec/tfsec:latest /src'
          } catch (err) {
            emailext(
              subject: "tfsec Scan Failed",
              body: "tfsec scan failed. Please check your Terraform code.",
              recipientProviders: [[$class: 'DevelopersRecipientProvider']]
            )
            error("Stopping pipeline due to tfsec failure")
          }

          // Run terraform validate
          try {
            sh '''
              docker run --rm \
                -v "$PWD/GCE:/workspace" \
                -w /workspace \
                hashicorp/terraform:1.9.7 \
                -c "terraform validate"
            '''
          } catch (err) {
            emailext(
              subject: "Terraform Validate Failed",
              body: "Terraform validation failed. Please fix errors before proceeding.",
              recipientProviders: [[$class: 'DevelopersRecipientProvider']]
            )
            error("Stopping pipeline due to terraform validate failure")
          }

          echo "Security and validation checks passed. Proceeding..."
        }
      }
    }

    stage('Check Drift') {
      when { expression { env.TF_CHANGED == 'true' } }
      steps {
        script {
          def driftDetected = false
          try {
            sh '''
              docker run --rm \
                -v "$PWD/GCE:/workspace" \
                -w /workspace \
                -e GOOGLE_APPLICATION_CREDENTIALS="/workspace/creds.json" \
                hashicorp/terraform:1.9.7 \
                terraform plan -detailed-exitcode -out=tfplan || true
            '''
            // The -detailed-exitcode returns 2 if drift found
            def planExitCode = sh(script: 'echo $?', returnStdout: true).trim()
            if (planExitCode == '2') {
              driftDetected = true
            }
          } catch (err) {
            error("Terraform plan failed: ${err}")
          }

          if (driftDetected) {
            emailext(
              subject: "Terraform Drift Detected",
              body: "Terraform drift detected! Manual intervention may be required.",
              recipientProviders: [[$class: 'DevelopersRecipientProvider']]
            )
            error("Stopping pipeline due to drift detection")
          } else {
            echo "No drift detected. Proceeding to apply."
          }
        }
      }
    }

    stage('Terraform Apply') {
      when { expression { env.TF_CHANGED == 'true' } }
      steps {
        withCredentials([file(credentialsId: 'GCP-credinals', variable: 'GCP_credinals')]) {
          script {
            def applyOutput = ''
            try {
              applyOutput = sh(
                script: '''
                  docker run --rm \
                    --entrypoint /bin/sh \
                    -v "$PWD:/workspace" \
                    -v "$GCP_credinals:/workspace/creds.json" \
                    -w /workspace/GCE \
                    -e GOOGLE_APPLICATION_CREDENTIALS="/workspace/creds.json" \
                    hashicorp/terraform:1.9.7 \
                    -c "terraform init -backend-config='bucket=my-tf-petclinic-backend' -backend-config='prefix=vpc/petclinic-backend' -backend-config='credentials=/workspace/creds.json' "
                ''',
                returnStdout: true
              ).trim()
            } catch (err) {
              emailext(
                subject: "Terraform Apply Failed",
                body: "Terraform apply failed! Check Jenkins console for details.",
                recipientProviders: [[$class: 'DevelopersRecipientProvider']]
              )
              error("Terraform apply failed")
            }

            // Send success email with output
            emailext(
              subject: "Terraform Apply Succeeded",
              body: "Terraform apply succeeded with output:\n\n${applyOutput}",
              recipientProviders: [[$class: 'DevelopersRecipientProvider']]
            )
          }
        }
      }
    }
  }
}
