
##variables for vpc Creation

variable "project_id" {
  type        = string
  description = "The GCP project ID"
}
variable "network_name" {
  description = "The name of the network being created"
  type        = string
}

variable "description" {
  type        = string
  description = "An optional description of this resource. The resource must be recreated to modify this field."
  default     = ""
}


variable "tags_db" {
  type        = list(string)
  description = "Network tags, provided as a list"
  default     = []
}



variable "subnets" {
  type = list(object({
    subnet_name                      = string
    subnet_ip                        = string
    subnet_region                    = string
    subnet_private_access            = optional(string)
    subnet_private_ipv6_access       = optional(string)
    subnet_flow_logs                 = optional(string)
    subnet_flow_logs_interval        = optional(string)
    subnet_flow_logs_sampling        = optional(string)
    subnet_flow_logs_metadata        = optional(string)
    subnet_flow_logs_filter          = optional(string)
    subnet_flow_logs_metadata_fields = optional(list(string))
    description                      = optional(string)
    purpose                          = optional(string)
    role                             = optional(string)
    stack_type                       = optional(string)
    ipv6_access_type                 = optional(string)
  }))
  description = "The list of subnets being created"
}

variable "routes" {
  type        = list(map(string))
  description = "List of routes being created in this VPC"
  default     = []
}



variable "ingress_rules" {
  description = "List of ingress rules. This will be ignored if variable 'rules' is non-empty"
  default     = []
  type = list(object({
    name                    = string
    description             = optional(string, null)
    disabled                = optional(bool, null)
    priority                = optional(number, null)
    destination_ranges      = optional(list(string), [])
    source_ranges           = optional(list(string), [])
    source_tags             = optional(list(string))
    source_service_accounts = optional(list(string))
    target_tags             = optional(list(string))
    target_service_accounts = optional(list(string))
    allow = optional(list(object({
      protocol = string
      ports    = optional(list(string))
    })), [])
    deny = optional(list(object({
      protocol = string
      ports    = optional(list(string))
    })), [])
    log_config = optional(object({
      metadata = string
    }))
  }))
}

variable "egress_rules" {
  description = "List of egress rules. This will be ignored if variable 'rules' is non-empty"
  default     = []
  type = list(object({
    name                    = string
    description             = optional(string, null)
    disabled                = optional(bool, null)
    priority                = optional(number, null)
    destination_ranges      = optional(list(string), [])
    source_ranges           = optional(list(string), [])
    source_tags             = optional(list(string))
    source_service_accounts = optional(list(string))
    target_tags             = optional(list(string))
    target_service_accounts = optional(list(string))

    allow = optional(list(object({
      protocol = string
      ports    = optional(list(string))
    })), [])
    deny = optional(list(object({
      protocol = string
      ports    = optional(list(string))
    })), [])
    log_config = optional(object({
      metadata = string
    }))
  }))
}


#COMPUTE ENGINE VARIABLES

variable "project_id_vm" {
  type        = string
  description = "The GCP project ID"
}

variable "region" {
  type        = string
  description = "Region where the instance template should be created."
}


variable "machine_type" {
  description = "Machine type to create, e.g. n1-standard-1"
  type        = string
  default     = "e2-micro-1"
}



variable "instance_description" {
  description = "Description of the generated instances"
  type        = string
  default     = ""
}


variable "tags" {
  type        = list(string)
  description = "Network tags, provided as a list"
  default     = []
}

variable "labels" {
  type        = map(string)
  description = "Labels, provided as a map"
  default     = {}
}



#######
# disk
#######
variable "source_image" {
  description = "Source disk image. If neither source_image nor source_image_family is specified, defaults to the latest public Rocky Linux 9 optimized for GCP image."
  type        = string
  default     = ""
}

variable "source_image_family" {
  description = "Source image family. If neither source_image nor source_image_family is specified, defaults to the latest public Rocky Linux 9 optimized for GCP image."
  type        = string
  default     = "rocky-linux-9-optimized-gcp"
}

variable "source_image_project" {
  description = "Project where the source image comes from. The default project contains Rocky Linux images."
  type        = string
  default     = "rocky-linux-cloud"
}

variable "disk_size_gb" {
  description = "Boot disk size in GB"
  type        = string
  default     = "100"
}

variable "disk_type" {
  description = "Boot disk type, can be either pd-ssd, local-ssd, or pd-standard"
  type        = string
  default     = "pd-balanced"
}





####################
# network_interface
####################
variable "network" {
  description = "The name or self_link of the network to attach this interface to. Use network attribute for Legacy or Auto subnetted networks and subnetwork for custom subnetted networks."
  type        = string
  default     = ""
}

variable "subnetwork" {
  description = "The name of the subnetwork to attach this interface to. The subnetwork must exist in the same region this instance will be created in. Either network or subnetwork must be provided."
  type        = string
  default     = ""
}

variable "subnetwork_project" {
  description = "The ID of the project in which the subnetwork belongs. If it is not provided, the provider project is used."
  type        = string
  default     = ""
}


variable "stack_type" {
  description = "The stack type for this network interface to identify whether the IPv6 feature is enabled or not. Values are `IPV4_IPV6` or `IPV4_ONLY`. Default behavior is equivalent to IPV4_ONLY."
  type        = string
  default     = null
}

variable "additional_networks" {
  description = "Additional network interface details for GCE, if any."
  default     = []
  type = list(object({
    network            = string
    subnetwork         = string
    subnetwork_project = string
    network_ip         = string
    nic_type           = string
    stack_type         = string
    queue_count        = number
    access_config = list(object({
      nat_ip       = string
      network_tier = string
    }))
    ipv6_access_config = list(object({
      network_tier = string
    }))
    alias_ip_range = list(object({
      ip_cidr_range         = string
      subnetwork_range_name = string
    }))
  }))
  validation {
    condition = alltrue([
      for ni in var.additional_networks : ni.nic_type == "GVNIC" || ni.nic_type == "VIRTIO_NET" || ni.nic_type == null
    ])
    error_message = "In the variable additional_networks, field \"nic_type\" must be either \"GVNIC\", \"VIRTIO_NET\" or null."
  }
  validation {
    condition = alltrue([
      for ni in var.additional_networks : ni.stack_type == "IPV4_ONLY" || ni.stack_type == "IPV4_IPV6" || ni.stack_type == null
    ])
    error_message = "In the variable additional_networks, field \"stack_type\" must be either \"IPV4_ONLY\", \"IPV4_IPV6\" or null."
  }
}



# ##################
# # service_account
# ##################

variable "service_account" {
  type = object({
    email  = string
    scopes = optional(set(string), ["cloud-platform"])
  })
  description = "Service account to attach to the instance. See https://www.terraform.io/docs/providers/google/r/compute_instance_template#service_account."
  default     = null
}

variable "create_service_account" {
  type        = bool
  description = "Create a new service account to attach to the instance. This is alternate to providing the service_account input variable. Please provide the service_account input if setting this to false."
  default     = true
}

variable "service_account_project_roles" {
  type        = list(string)
  description = "Roles to grant to the newly created cloud run SA in specified project. Should be used with create_service_account set to true and no input for service_account"
  default     = []
}


###########################
# Public IP
###########################
variable "access_config" {
  description = "Access configurations, i.e. IPs via which the VM instance can be accessed via the Internet."
  type = list(object({
    nat_ip       = optional(string)
    network_tier = string
  }))
  default = []
}
###########
# metadata
###########

variable "startup_script" {
  description = "User startup script to run when instances spin up"
  type        = string
  default     = "null"
}

##GCE Resource VM WEB
variable "name_web" {
  description = "User startup script to run when instances spin up"
  type        = string
}
variable "zone_vm" {
  description = "User startup script to run when instances spin up"
  type        = string
  default     = "us-west1"
}

variable "subnet_web" {
  description = "User startup script to run when instances spin up"
  type        = string
}
variable "startup_script_web" {
  description = "User startup script to run when instances spin up"
  type        = string
  default     = "ls"
}

variable "access_config_web" {
  description = "User startup script to run when instances spin up"
  type        = string
  default     = "PREMIUM"
}

##GCE Resource VM WEB2
variable "name_web2" {
  description = "User startup script to run when instances spin up"
  type        = string
}
variable "zone_vm" {
  description = "User startup script to run when instances spin up"
  type        = string
  default     = "us-west1"
}

variable "subnet_web2" {
  description = "User startup script to run when instances spin up"
  type        = string
}
variable "startup_script_web2" {
  description = "User startup script to run when instances spin up"
  type        = string
  default     = "ls"
}

variable "access_config_web2" {
  description = "User startup script to run when instances spin up"
  type        = string
  default     = "PREMIUM"
}




#CLoudSql variables 

variable "project_id_sql" {
  description = "The project ID to manage the Cloud SQL resources"
  type        = string
}

// required
variable "region_sql" {
  description = "The region of the Cloud SQL resources"
  type        = string
  default     = "us-central1"
}

variable "name_sql" {
  type        = string
  description = "The name of the Cloud SQL resources"
}

variable "edition" {
  description = "The edition of the instance, can be ENTERPRISE or ENTERPRISE_PLUS."
  type        = string
  default     = null
}

// required
variable "database_version" {
  description = "The database version to use"
  type        = string
}

variable "maintenance_version" {
  description = "The current software version on the instance. This attribute can not be set during creation. Refer to available_maintenance_versions attribute to see what maintenance_version are available for upgrade. When this attribute gets updated, it will cause an instance restart. Setting a maintenance_version value that is older than the current one on the instance will be ignored"
  type        = string
  default     = null
}

variable "availability_type" {
  description = "The availability type for the master instance. Can be either `REGIONAL` or `null`."
  type        = string
  default     = "REGIONAL"
}

variable "enable_default_db" {
  description = "Enable or disable the creation of the default database"
  type        = bool
  default     = true
}

variable "db_name_sql" {
  description = "The name of the default database to create. This should be unique per Cloud SQL instance."
  type        = string
  default     = "default"
}

variable "enable_default_user" {
  description = "Enable or disable the creation of the default user"
  type        = bool
  default     = true
}

variable "user_name_sql" {
  description = "The name of the default user"
  type        = string
  default     = "default"
}

variable "user_password_sql" {
  description = "The password for the default user. If not set, a random one will be generated and available in the generated_user_password output variable."
  type        = string
  sensitive   = true
}

variable "user_host" {
  description = "The host for the default user"
  type        = string
  default     = "%"
}

variable "root_password" {
  description = "MySQL password for the root user."
  type        = string
  default     = null
}

variable "deletion_protection_sql" {
  description = "Used to block Terraform from deleting a SQL Instance."
  type        = bool
  default     = true
}

variable "user_deletion_policy" {
  description = "The deletion policy for the user. Setting ABANDON allows the resource to be abandoned rather than deleted. This is useful for Postgres, where users cannot be deleted from the API if they have been granted SQL roles. Possible values are: \"ABANDON\"."
  type        = string
  default     = null
}

variable "data_cache_enabled" {
  description = "Whether data cache is enabled for the instance. Defaults to false. Feature is only available for ENTERPRISE_PLUS tier and supported database_versions"
  type        = bool
  default     = false
}

variable "additional_databases" {
  description = "A list of databases to be created in your cluster"
  type = list(object({
    name      = string
    charset   = string
    collation = string
  }))
  default = []
}

variable "additional_users" {
  description = "A list of users to be created in your cluster. A random password would be set for the user if the `random_password` variable is set."
  type = list(object({
    name            = string
    password        = string
    random_password = bool
    type            = string
    host            = string
  }))
  default = []
  validation {
    condition     = length([for user in var.additional_users : false if user.random_password == true && (user.password != null && user.password != "")]) == 0
    error_message = "You cannot set both password and random_password, choose one of them."
  }
}


variable "random_instance_name" {
  type        = bool
  description = "Sets random suffix at the end of the Cloud SQL resource name"
  default     = false
}

variable "replica_database_version" {
  description = "The read replica database version to use. This var should only be used during a database update. The update sequence 1. read-replica 2. master, setting this to an updated version will cause the replica to update, then you may update the master with the var database_version and remove this field after update is complete"
  type        = string
  default     = ""
}

// optional
variable "master_instance_name" {
  description = "The name of the existing instance that will act as the master in the replication setup."
  type        = string
  default     = null
}

//optional
variable "failover_dr_replica_name" {
  type        = string
  description = "If the instance is a primary instance, then this field identifies the disaster recovery (DR) replica. The standard format of this field is \"your-project:your-instance\". You can also set this field to \"your-instance\", but cloud SQL backend will convert it to the aforementioned standard format."
  default     = null
}

// optional
variable "instance_type" {
  description = "Users can upgrade a read replica instance to a stand-alone Cloud SQL instance with the help of instance_type. To promote, users have to set the instance_type property as CLOUD_SQL_INSTANCE and remove/unset master_instance_name and replica_configuration from instance configuration. This operation might cause your instance to restart."
  type        = string
  default     = null
}

// Master
variable "tier" {
  description = "The tier for the master instance, for ADC its defualt value will be db-perf-optimized-N-8 which is tier value for edtion ENTERPRISE_PLUS, if user wants to change the edition, he should chose compatible tier."
  type        = string
  default     = "db-n1-standard-1"
}

variable "zone" {
  description = "The zone for the master instance, it should be something like: `us-central1-a`, `us-east1-c`."
  type        = string
  default     = null
}

variable "secondary_zone" {
  type        = string
  description = "The preferred zone for the secondary/failover instance, it should be something like: `us-central1-a`, `us-east1-c`."
  default     = null
}

variable "follow_gae_application" {
  type        = string
  description = "A Google App Engine application whose zone to remain in. Must be in the same region as this instance."
  default     = null
}

variable "activation_policy" {
  description = "The activation policy for the master instance. Can be either `ALWAYS`, `NEVER` or `ON_DEMAND`."
  type        = string
  default     = "ALWAYS"
}

variable "deletion_protection_enabled" {
  description = "Enables protection of an instance from accidental deletion across all surfaces (API, gcloud, Cloud Console and Terraform)."
  type        = bool
  default     = false
}

variable "read_replica_deletion_protection_enabled" {
  description = "Enables protection of a read replica from accidental deletion across all surfaces (API, gcloud, Cloud Console and Terraform)."
  type        = bool
  default     = false
}

variable "disk_autoresize" {
  description = "Configuration to increase storage size"
  type        = bool
  default     = true
}

variable "disk_autoresize_limit" {
  description = "The maximum size to which storage can be auto increased."
  type        = number
  default     = 0
}

variable "db_disk_size" {
  description = "The disk size (in GB) for the master instance"
  type        = number
  default     = 10
}

variable "db_disk_type" {
  description = "The disk type for the master instance."
  type        = string
  default     = "PD_SSD"
}

variable "pricing_plan" {
  description = "The pricing plan for the master instance."
  type        = string
  default     = "PER_USE"
}

variable "maintenance_window_day" {
  description = "The day of week (1-7) for the master instance maintenance."
  type        = number
  default     = 1
}

variable "maintenance_window_hour" {
  description = "The hour of day (0-23) maintenance window for the master instance maintenance."
  type        = number
  default     = 23
}

variable "maintenance_window_update_track" {
  description = "The update track of maintenance window for the master instance maintenance. Can be either `canary` or `stable`."
  type        = string
  default     = "canary"
}

variable "database_flags" {
  description = "List of Cloud SQL flags that are applied to the database server. See [more details](https://cloud.google.com/sql/docs/mysql/flags)"
  type = list(object({
    name  = string
    value = string
  }))
  default = []
}


variable "user_labels" {
  type        = map(string)
  default     = {}
  description = "The key/value labels for the master instances."
}

variable "deny_maintenance_period" {
  description = "The Deny Maintenance Period fields to prevent automatic maintenance from occurring during a 90-day time period. List accepts only one value. See [more details](https://cloud.google.com/sql/docs/mysql/maintenance)"
  type = list(object({
    end_date   = string
    start_date = string
    time       = string
  }))
  default = []
}

variable "backup_configuration" {
  description = "The backup_configuration settings subblock for the database setings"
  type = object({
    binary_log_enabled             = optional(bool, false)
    enabled                        = optional(bool, false)
    start_time                     = optional(string)
    location                       = optional(string)
    transaction_log_retention_days = optional(string)
    retained_backups               = optional(number)
    retention_unit                 = optional(string)
  })
  default = {}
}

variable "insights_config" {
  description = "The insights_config settings for the database."
  type = object({
    query_plans_per_minute  = number
    query_string_length     = number
    record_application_tags = bool
    record_client_address   = bool
  })
  default = null
}

variable "ip_configuration" {
  description = "The ip_configuration settings subblock"
  type = object({
    authorized_networks                           = optional(list(map(string)), [])
    ipv4_enabled                                  = optional(bool, true)
    private_network                               = optional(string)
    ssl_mode                                      = optional(string)
    allocated_ip_range                            = optional(string)
    enable_private_path_for_google_cloud_services = optional(bool, false)
    psc_enabled                                   = optional(bool, false)
    psc_allowed_consumer_projects                 = optional(list(string), [])
  })
  default = {}
}

variable "password_validation_policy_config" {
  description = "The password validation policy settings for the database instance."
  type = object({
    enable_password_policy      = bool
    min_length                  = optional(number)
    complexity                  = optional(string)
    disallow_username_substring = optional(bool)
    reuse_interval              = optional(number)
  })
  default = null
}

// Read Replicas
variable "read_replicas" {
  description = "List of read replicas to create. Encryption key is required for replica in different region. For replica in same region as master set encryption_key_name = null"
  type = list(object({
    name                  = string
    name_override         = optional(string)
    tier                  = optional(string)
    edition               = optional(string)
    availability_type     = optional(string)
    zone                  = optional(string)
    disk_type             = optional(string)
    disk_autoresize       = optional(bool)
    disk_autoresize_limit = optional(number)
    disk_size             = optional(string)
    user_labels           = map(string)
    database_flags = list(object({
      name  = string
      value = string
    }))
    backup_configuration = optional(object({
      binary_log_enabled             = bool
      transaction_log_retention_days = string
    }))
    insights_config = optional(object({
      query_plans_per_minute  = number
      query_string_length     = number
      record_application_tags = bool
      record_client_address   = bool
    }))
    ip_configuration = object({
      authorized_networks                           = optional(list(map(string)), [])
      ipv4_enabled                                  = optional(bool)
      private_network                               = optional(string)
      ssl_mode                                      = optional(string)
      allocated_ip_range                            = optional(string)
      enable_private_path_for_google_cloud_services = optional(bool, false)
      psc_enabled                                   = optional(bool, false)
      psc_allowed_consumer_projects                 = optional(list(string), [])
    })
    encryption_key_name = optional(string)
    data_cache_enabled  = optional(bool)
  }))
  default = []
}

variable "read_replica_name_suffix" {
  description = "The optional suffix to add to the read instance name"
  type        = string
  default     = ""
}

variable "db_charset" {
  description = "The charset for the default database"
  type        = string
  default     = ""
}

variable "db_collation" {
  description = "The collation for the default database. Example: 'utf8_general_ci'"
  type        = string
  default     = ""
}

variable "create_timeout" {
  description = "The optional timout that is applied to limit long database creates."
  type        = string
  default     = "30m"
}

variable "update_timeout" {
  description = "The optional timout that is applied to limit long database updates."
  type        = string
  default     = "30m"
}

variable "delete_timeout" {
  description = "The optional timout that is applied to limit long database deletes."
  type        = string
  default     = "30m"
}

variable "encryption_key_name" {
  description = "The full path to the encryption key used for the CMEK disk encryption"
  type        = string
  default     = null
}

variable "module_depends_on" {
  description = "List of modules or resources this module depends on."
  type        = list(any)
  default     = []
}

variable "read_replica_deletion_protection" {
  description = "Used to block Terraform from deleting replica SQL Instances."
  type        = bool
  default     = false
}

variable "enable_random_password_special" {
  description = "Enable special characters in generated random passwords."
  type        = bool
  default     = false
}

variable "connector_enforcement" {
  description = "Enforce that clients use the connector library"
  type        = bool
  default     = false
}

variable "enable_google_ml_integration" {
  description = "Enable database ML integration"
  type        = bool
  default     = false
}

variable "enable_dataplex_integration" {
  description = "Enable database Dataplex integration"
  type        = bool
  default     = false
}

variable "database_integration_roles" {
  description = "The roles required by default database instance service account for integration with GCP services"
  type        = list(string)
  default     = []
}


#cloud sql private access variables 

variable "name_psc" {
  description = "name for private service to use cloudsql to vm"
  type        = string
}
variable "purpose_psc" {
  description = "psc used for like vpc peering "
  type        = string

}
variable "address_type_psc" {
  description = "internal or external for peering"
  type        = string

}
variable "prefix_length_psc" {
  description = "subnet range of psc not overlap the vpc subnet "
  type        = number

}
variable "address_psc" {
  description = "range of subnet to make a perring for intenal"
  type        = string

}


