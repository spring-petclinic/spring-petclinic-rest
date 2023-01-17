terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.0"
    }
  }
}

provider "aws" {
  shared_config_files      = ["~/.aws/config"]
  shared_credentials_files = ["~/.aws/credentials"]
}

# resource "aws_instance" "example" {
#     ami = "ami-0beb6fc68811e5682"
#     instance_type = "t2.micro"
# }
