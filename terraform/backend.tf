terraform {
  backend "gcs" {
    bucket = "my-tf-petclinic-backend"
    prefix = "vpc/petclinic-backend"
  }
}
