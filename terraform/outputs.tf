#vpc name
output "network_name" {
  value       = module.network.network_name
  description = "The name of the VPC being created"
}

##Cloud sql output
output "db_instance_ip" {
  value = module.mysql-db.private_ip_address
}

output "db_name" {
  value = module.mysql-db.instance_name
}

output "db_user" {
  value = module.mysql-db.additional_users
}