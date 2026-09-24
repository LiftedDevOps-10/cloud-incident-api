terraform {
  backend "s3" {
    bucket       = "cloud-incident-api-tfstate-2026"
    key          = "cloud-incident-api/terraform.tfstate"
    region       = "eu-north-1"
    use_lockfile = true
    encrypt      = true
  }

  required_providers {
    aws = {
      source = "hashicorp/aws"
    }
  }

  required_version = ">= 1.5.0"
}

provider "aws" {
  region = "eu-north-1"
}
