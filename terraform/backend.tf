terraform {
  backend "gcs" {
    bucket = "my-tf-petclinic-backend"
    prefix = "petclinic/petclinic-backend"
  }
}
