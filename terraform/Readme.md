# GCP PetClinic Infrastructure

This Terraform project sets up core Google Cloud Platform (GCP) infrastructure for a PetClinic application. It includes a VPC network, subnets, firewall rules, a Compute Engine VM for the web application, and a Cloud SQL (MySQL) database.

## 🚀 Project Overview

This setup provisions the following key GCP resources:

* **VPC Network:** A dedicated network for your application.
* **Subnets:** Separated subnets for web and database tiers.
* **Firewall Rules:** Basic security rules for application access.
* **Compute Engine VM:** An Ubuntu virtual machine for the web application.
* **Cloud SQL (MySQL):** A managed MySQL database instance with private connectivity.

## 📂 Repository Structure
├── main.tf                 # Main Terraform configuration
├── variables.tf            # Variable definitions
├── outputs.tf              # Output declarations
├── providers.tf            # Provider configuration (GCP)
├── terraform.tfvars.example  # Example variable values (rename to terraform.tfvars)
└── README.md               # This file

## 📋 Prerequisites

Before you begin, ensure you have:

1.  **Terraform CLI:** Installed (version 1.0.0 or higher recommended).
2.  **Google Cloud SDK (gcloud CLI):** Installed and authenticated to your GCP project.
    * Run `gcloud auth application-default login`
    * Set your project: `gcloud config set project your-gcp-project-id` (replace `your-gcp-project-id`)
3.  **GCP Project:** An active GCP project with billing enabled and necessary APIs enabled (Compute Engine API, Cloud SQL Admin API, Service Networking API).

## 🚀 Quick Setup Guide

Follow these simple steps to get started:

1.  **Clone the Repository:**
    ```bash
    git clone <your-repo-url>
    cd <your-repo-directory>
    ```

2.  **Prepare Variables:**
    Copy the example variable file. This is where you'll define your specific values.
    ```bash
    cp terraform.tfvars.example terraform.tfvars
    ```
    Now, **edit `terraform.tfvars`** in your text editor and provide your desired values. Refer to the sample below.

3.  **Initialize Terraform:**
    This command downloads the necessary GCP provider plugin.
    ```bash
    terraform init
    ```

4.  **Review the Plan (Dry Run):**
    Always check what Terraform plans to do before applying changes.
    ```bash
    terraform plan
    ```
    Carefully review the output. It will show you exactly what resources will be created, modified, or destroyed.

5.  **Apply the Configuration:**
    If the plan looks correct, apply the changes to provision your infrastructure.
    ```bash
    terraform apply
    ```
    Terraform will ask for confirmation. Type `yes` and press Enter.

6.  **View Outputs:**
    After a successful apply, Terraform will display important information like IP addresses.
    ```bash
    terraform output
    ```

7.  **Destroy Infrastructure (Cleanup):**
    To remove all resources created by this configuration (use with extreme caution!):
    ```bash
    terraform destroy
    ```
    Type `yes` to confirm.

## 📝 Sample `terraform.tfvars`

Create a file named `terraform.tfvars` in your project root and populate it with values similar to this. **Replace placeholders like `your-gcp-project-id` and `your-secure-db-password`.**

```terraform
# Project and Network Configuration
project_id   = "your-gcp-project-id" # CHANGE THIS to your actual GCP Project ID
network_name = "my-petclinic-vpc"
description  = "VPC for PetClinic application"

subnets = [
  {
    subnet_name           = "web-subnet"
    subnet_ip             = "10.10.1.0/24"
    subnet_region         = "us-west1"
    subnet_private_access = "true"
    subnet_flow_logs      = "false"
    description           = "Subnet for web application servers"
  },
  {
    subnet_name           = "db-subnet"
    subnet_ip             = "10.10.2.0/24"
    subnet_region         = "us-west1"
    subnet_private_access = "false" # Cloud SQL private IP uses service networking
    subnet_flow_logs      = "false"
    description           = "Subnet for database connections (internal)"
  }
]

ingress_rules = [
  {
    allow = [{ ports = ["22", "80", "443"], protocol = "tcp" }]
    deny               = []
    description        = "Allow SSH, HTTP, HTTPS to web servers"
    destination_ranges = ["10.10.1.0/24"] # Target web subnet
    disabled           = false
    name               = "allow-web-access"
    priority           = 1000
    source_ranges      = ["0.0.0.0/0"] # Allow from anywhere (consider restricting)
    target_tags        = ["web"]
  },
  {
    allow = [{ ports = ["3306"], protocol = "tcp" }]
    deny          = []
    description   = "Allow web servers to connect to DB"
    disabled      = false
    name          = "allow-db-access"
    priority      = 1000
    source_ranges = ["10.10.1.0/24"] # Only from web subnet
    target_tags   = ["db"] # Assuming DB VMs, but primarily for Cloud SQL private IP
  },
  {
    allow = []
    deny = [{ ports = ["0-65535"], protocol = "tcp" }]
    description   = "Deny all other ingress to DB instances"
    name          = "deny-all-ingress-db"
    priority      = 65535
    source_ranges = ["0.0.0.0/0"]
    target_tags   = ["db"]
  }
]

egress_rules = [
  {
    allow = []
    deny = [{ ports = ["0-65535"], protocol = "tcp" }]
    description   = "Deny all other egress from DB instances"
    name          = "deny-all-egress-db"
    priority      = 65535
    source_ranges = ["0.0.0.0/0"]
    target_tags   = ["db"]
  }
]

# Compute Engine VM Variables
project_id_vm = "your-gcp-project-id" # CHANGE THIS if different from main project_id
region        = "us-west1"
machine_type  = "e2-micro"
source_image  = "projects/ubuntu-os-cloud/global/images/ubuntu-minimal-2410-oracular-amd64-v20250501" # Ubuntu 24.10 Minimal
disk_size_gb  = "10"
tags          = ["web"] # For firewall rule targeting
disk_type     = "pd-balanced"
network       = "my-petclinic-vpc" # Must match network_name above
subnetwork    = "[https://www.googleapis.com/compute/v1/projects/your-gcp-project-id/regions/us-west1/subnetworks/web-subnet](https://www.googleapis.com/compute/v1/projects/your-gcp-project-id/regions/us-west1/subnetworks/web-subnet)" # CHANGE project_id and subnet name
stack_type    = "IPV4_ONLY"
access_config = [{ network_tier = "PREMIUM" }]
create_service_account = false # Assuming you'll attach an existing SA or manage separately

name_web           = "petclinic-web-server"
zone_vm            = "us-west1-a"
subnet_web         = "[https://www.googleapis.com/compute/v1/projects/your-gcp-project-id/regions/us-west1/subnetworks/web-subnet](https://www.googleapis.com/compute/v1/projects/your-gcp-project-id/regions/us-west1/subnetworks/web-subnet)" # CHANGE project_id and subnet name
access_config_web  = "PREMIUM"
startup_script_web = "#!/bin/bash\necho 'Hello from PetClinic VM' > /var/log/startup_message.txt" # Simple example

# Private Service Connection (PSC) for Cloud SQL
name_psc          = "petclinic-psc-range"
purpose_psc       = "VPC_PEERING"
address_type_psc  = "INTERNAL"
prefix_length_psc = 24
address_psc       = "10.10.3.0" # Make sure this doesn't overlap with other subnets

# Cloud SQL Variables
name_sql           = "petclinic-db-sql"
region_sql         = "us-west1"
database_version   = "MYSQL_8_0"
tier               = "db-f1-micro" # Use a smaller tier for dev/test
db_disk_size       = 10
db_disk_type       = "PD_SSD"
availability_type  = "ZONAL"
project_id_sql     = "your-gcp-project-id" # CHANGE THIS
backup_configuration = {
  enabled                      = true
  start_time                   = "03:00"
  point_in_time_recovery_enabled = true
  retained_backups             = 7
  retention_unit               = "DAYS"
}
user_name_sql     = "appuser"
user_password_sql = "your-secure-db-password" # CHANGE THIS to a strong, unique password
db_name_sql       = "petclinicdb"

# Cloud SQL IP Configuration (uses PSC for private IP)
ip_configuration = {
  ipv4_enabled       = false # Set to true if you need public IP
  # private_network and allocated_ip_range will be managed by the main.tf
  # due to referring to dynamically created resources (module.vpc.network_self_link and google_compute_global_address.psc_ip_range.name)
  psc_enabled        = true
}
deletion_protection_sql = false # Set to true for production to prevent accidental emulation


---

**Part 4: Best Practices & Important Considerations**

```markdown
## ✨ Best Practices

* **State Management:** For any real-world use (especially CI/CD or team collaboration), **do NOT use the local `terraform.tfstate` file.** Configure a [remote backend](https://developer.hashicorp.com/terraform/language/state/remote) like a **GCS bucket** to store your state securely and enable locking.
    ```terraform
    # Example for GCS backend (add this to main.tf or providers.tf)
    terraform {
      backend "gcs" {
        bucket  = "your-terraform-state-bucket" # Create this bucket manually first
        prefix  = "petclinic-gcp/terraform.tfstate"
      }
    }
    ```
* **Secrets Management:** **Never hardcode sensitive values** like database passwords or API keys directly in `terraform.tfvars` or your `.tf` files, especially if the repository is public. Use a secrets manager (e.g., GCP Secret Manager, HashiCorp Vault, environment variables in CI/CD like `TF_VAR_db_password`) to inject these securely at runtime.
* **Least Privilege:** Configure service accounts (for CI/CD and VMs) with only the minimum necessary permissions required to provision and manage resources.
* **Regular Planning:** Always run `terraform plan` before `terraform apply` to understand the exact changes Terraform will make.
* **CI/CD Integration:** Integrate `terraform validate` and `terraform plan` into your CI/CD pipeline to ensure code quality and review changes before deployment. For `terraform apply`, use `-auto-approve` only in trusted CI/CD environments where the plan has been reviewed.
* **Resource Naming:** Use consistent and descriptive naming conventions for your resources.

## ⚠️ Important Considerations

* **Costs:** Running these resources on GCP will incur charges. Monitor your GCP billing dashboard regularly.
* **Security:** The provided firewall rules are basic. Adjust them to the principle of least privilege for your actual application's needs. For example, `source_ranges = ["0.0.0.0/0"]` for SSH is highly insecure for production and should be restricted to known IP ranges.
* **Cloud SQL Password:** The sample `user_password_sql` is `your-secure-db-password`. **Change this to a truly strong, unique password** and manage it as a secret, not in plain text.
* **Deletion Protection:** For production Cloud SQL instances, strongly consider setting `deletion_protection_sql = true` to prevent accidental deletion.
* **VM Startup Script:** The `startup_script_web` in the sample (`"touch my.txt"`) is minimal. In a real scenario, this script would handle installing your web server, deploying your application, etc.

## 📊 Outputs

Upon successful `terraform apply`, the following important outputs will be available:

* `vm_external_ip`: The external IP address of the web Compute Engine instance (if applicable).
* `sql_instance_connection_name`: The connection name for the Cloud SQL instance.
* `sql_instance_private_ip_address`: The private IP address of the Cloud SQL instance.
* `vpc_network_self_link`: The self-link of the created VPC network.

You can retrieve these outputs after applying:

```bash
terraform output