# --- VPC Network ---
module "network" {
  source  = "terraform-google-modules/network/google"
  version = "11.1.0"

  project_id    = var.project_id
  network_name  = var.network_name
  description   = var.description
  subnets       = var.subnets
  ingress_rules = var.ingress_rules
  egress_rules  = var.egress_rules
}


# --- Instance Template ---
module "web_instance_template" {
  source  = "terraform-google-modules/vm/google//modules/instance_template"
  version = "13.2.4"

  project_id             = var.project_id_vm
  region                 = var.region
  machine_type           = var.machine_type
  source_image           = var.source_image
  disk_size_gb           = var.disk_size_gb
  disk_type              = var.disk_type
  network                = module.network.network_self_link
  subnetwork             = var.subnetwork
  stack_type             = var.stack_type
  access_config          = var.access_config
  startup_script         = var.startup_script
  tags                   = var.tags
  create_service_account = var.create_service_account
}

# --- Compute Instance from Template ---
resource "google_compute_instance_from_template" "web1" {
  name                     = var.name_web
  project                  = var.project_id_vm
  zone                     = var.zone_vm
  source_instance_template = module.web_instance_template.self_link

  network_interface {
    subnetwork = var.subnet_web
    access_config {
      network_tier = var.access_config_web
    }
  }

  metadata_startup_script = var.startup_script_web
}


# --- Compute Instance from Template ---
resource "google_compute_instance_from_template" "web2" {
  name                     = var.name_web2
  project                  = var.project_id_vm
  zone                     = var.zone_vm
  source_instance_template = module.web_instance_template.self_link

  network_interface {
    subnetwork = var.subnet_web2
    access_config {
      network_tier = var.access_config_web2
    }
  }

  metadata_startup_script = var.startup_script_web2
}




# --- Global Address for PSC ---
resource "google_compute_global_address" "psc_ip_range" {
  name          = var.name_psc
  purpose       = var.purpose_psc
  address_type  = var.address_type_psc
  prefix_length = var.prefix_length_psc
  address       = var.address_psc
  network       = module.network.network_self_link
  project       = var.project_id_sql
}


resource "google_service_networking_connection" "private_vpc_connection" {
  network                 = module.network.network_self_link
  service                 = "servicenetworking.googleapis.com"
  reserved_peering_ranges = [google_compute_global_address.psc_ip_range.name]

  depends_on = [google_compute_global_address.psc_ip_range]
}

# --- MySQL Cloud SQL ---
# tfsec:ignore:google-sql-encrypt-in-transit-data This Cloud SQL instance uses private networking only.
module "mysql-db" {
  source  = "terraform-google-modules/sql-db/google//modules/mysql"
  version = "~> 25.2"

  project_id       = var.project_id_sql
  name             = var.name_sql
  region           = var.region_sql
  database_version = var.database_version

  tier              = var.tier
  disk_size         = var.db_disk_size
  disk_type         = var.db_disk_type
  availability_type = var.availability_type

  backup_configuration = var.backup_configuration

  user_name     = var.user_name_sql
  user_password = var.user_password_sql
  db_name       = var.db_name_sql

  ip_configuration = {
    ipv4_enabled       = false
    private_network    = module.network.network_self_link
    allocated_ip_range = google_compute_global_address.psc_ip_range.name
    psc_enabled        = true
    ssl_mode           = "ENCRYPTED_ONLY"
    requireSsl=true

  }


  deletion_protection = var.deletion_protection_sql
  depends_on          = [google_service_networking_connection.private_vpc_connection] #befor the sql insyance private connection need to create
}

