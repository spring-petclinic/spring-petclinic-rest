project_id   = "earnest-scene-454409-t5"
network_name = "petclinic-vpc"
description  = "used for the cicd"
subnets = [{ subnet_name = "web-applications"
  subnet_ip             = "10.10.1.0/24"
  subnet_region         = "us-west1"
  subnet_private_access = "true"
  subnet_flow_logs      = "false"
  description           = "This subnet is used to host web apps"
  }, { subnet_name      = "db-applications"
  subnet_ip             = "10.10.2.0/24"
  subnet_region         = "us-west1"
  subnet_private_access = "false"
  subnet_flow_logs      = "false"
  description           = "This subnet is used to DB apps"
}]

ingress_rules = [{
  allow = [{
    ports    = ["22", "80", "443","2377","7946"]
    protocol = "tcp"
  }]
  deny               = []
  description        = "for ssh,docker swaem and website access"
  destination_ranges = ["10.10.1.0/24"]
  disabled           = false
  name               = "allow-ssh"
  priority           = 1000
  source_ranges      = ["0.0.0.0/0"]
  target_tags        = ["web"]
  },{
  allow = [{
    ports    = [4789]
    protocol = "udp"
  }]
  deny               = []
  description        = "for docker swarm"
  destination_ranges = ["10.10.1.0/24"]
  disabled           = false
  name               = "allow-ssh"
  priority           = 1000
  source_ranges      = ["0.0.0.0/0"]
  target_tags        = ["web"]
  }
  , {
    allow = [{
      ports    = ["3306"]
      protocol = "tcp"
    }]
    deny          = []
    description   = "for DB connection"
    disabled      = false
    name          = "allow-db"
    priority      = 1000
    source_ranges = ["10.10.1.0/24"]
    target_tags   = ["web"]
  },
  {
    allow = []
    deny = [{
      ports    = ["0-65535"]
      protocol = "tcp"
    }]
    description   = "restriction the connection"
    name          = "deny-all"
    priority      = 65535
    source_ranges = ["0.0.0.0/0"]
    target_tags   = ["db"]
}]

egress_rules = [{
  allow = []
  deny = [{
    ports    = ["0-65535"]
    protocol = "tcp"
  }]
  description   = "restriction the connection"
  name          = "deny-all"
  priority      = 65535
  source_ranges = ["0.0.0.0/0"]
  target_tags   = ["db"]
}]

##Compute engine 
project_id_vm = "earnest-scene-454409-t5"
region        = "us-west1"
machine_type  = "e2-micro"
source_image = "projects/ubuntu-os-cloud/global/images/ubuntu-minimal-2204-jammy-v20250605"
disk_size_gb  = "10"

tags       = ["web"]
disk_type  = "pd-balanced"
network    = "petclinic-vpc"
subnetwork = "https://www.googleapis.com/compute/v1/projects/earnest-scene-454409-t5/regions/us-west1/subnetworks/web-applications"
stack_type = "IPV4_ONLY"
access_config = [
  {
    network_tier = "PREMIUM"
  }
]
create_service_account = false






# Web VM variables
name_web           = "web-hosting"
zone_vm            = "us-west1-a"
subnet_web         = "https://www.googleapis.com/compute/v1/projects/earnest-scene-454409-t5/regions/us-west1/subnetworks/web-applications"
access_config_web  = "PREMIUM"
startup_script_web = ""

# Web VM variables
name_web2          = "web-hosting"
subnet_web2         = "https://www.googleapis.com/compute/v1/projects/earnest-scene-454409-t5/regions/us-west1/subnetworks/web-applications"
access_config_web2  = "PREMIUM"
startup_script_web2 = ""




##ip range for private peering
name_psc          = "psc-ip-range"
purpose_psc       = "VPC_PEERING"
address_type_psc  = "INTERNAL"
prefix_length_psc = 24
address_psc       = "10.10.3.0"



name_sql         = "petclinic-db-sql"
region_sql       = "us-west1"
database_version = "MYSQL_8_0"

tier              = "db-custom-2-8192"
db_disk_size      = 10
db_disk_type      = "PD_SSD"
availability_type = "ZONAL"
project_id_sql    = "earnest-scene-454409-t5"
backup_configuration = {
   enabled                        = true
      start_time                     = "03:00"
      point_in_time_recovery_enabled = true
      retained_days                  = 7  #it keeps a backup 7 days and not support with retained_unit it means keep a number of backups 
}

# Optional user, password, and DB setup
#don't use hardcorede passwords 
user_name_sql     = "appuser"
user_password_sql = "123"
db_name_sql       = "petclinicdb"

#this if for delete the sql related without this we can't destroy it's for onpy learning purpose enalbe on prodution level
deletion_protection_sql = false