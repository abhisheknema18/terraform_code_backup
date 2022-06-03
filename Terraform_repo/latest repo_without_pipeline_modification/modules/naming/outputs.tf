
output "unique-seed" {
  value = coalesce(var.unique-seed, local.random_safe_generation)
}

output "validation" {
  value = local.validation
}
output "application_gateway" {
  value = local.az.application_gateway
}

output "application_security_group" {
  value = local.az.application_security_group
}

output "automation_account" {
  value = local.az.automation_account
}

output "automation_certificate" {
  value = local.az.automation_certificate
}

output "automation_credential" {
  value = local.az.automation_credential
}

output "automation_runbook" {
  value = local.az.automation_runbook
}

output "automation_schedule" {
  value = local.az.automation_schedule
}

output "automation_variable" {
  value = local.az.automation_variable
}

output "availability_set" {
  value = local.az.availability_set
}

output "bastion_host" {
  value = local.az.bastion_host
}

output "container_group" {
  value = local.az.container_group
}

output "container_registry" {
  value = local.az.container_registry
}

output "container_registry_webhook" {
  value = local.az.container_registry_webhook
}

output "disk_encryption_set" {
  value = local.az.disk_encryption_set
}

output "dns_a_record" {
  value = local.az.dns_a_record
}

output "dns_aaaa_record" {
  value = local.az.dns_aaaa_record
}

output "dns_caa_record" {
  value = local.az.dns_caa_record
}

output "dns_cname_record" {
  value = local.az.dns_cname_record
}

output "dns_mx_record" {
  value = local.az.dns_mx_record
}

output "dns_ns_record" {
  value = local.az.dns_ns_record
}

output "dns_ptr_record" {
  value = local.az.dns_ptr_record
}

output "dns_txt_record" {
  value = local.az.dns_txt_record
}

output "dns_zone" {
  value = local.az.dns_zone
}

output "firewall" {
  value = local.az.firewall
}

output "firewall_policy" {
  value = local.az.firewall_policy
}

output "firewall_application_rule_collection" {
  value = local.az.firewall_application_rule_collection
}

output "firewall_ip_configuration" {
  value = local.az.firewall_ip_configuration
}

output "firewall_nat_rule_collection" {
  value = local.az.firewall_nat_rule_collection
}

output "firewall_network_rule_collection" {
  value = local.az.firewall_network_rule_collection
}

output "frontdoor" {
  value = local.az.frontdoor
}

output "frontdoor_firewall_policy" {
  value = local.az.frontdoor_firewall_policy
}

output "image" {
  value = local.az.image
}

output "key_vault" {
  value = local.az.key_vault
}

output "key_vault_certificate" {
  value = local.az.key_vault_certificate
}

output "key_vault_key" {
  value = local.az.key_vault_key
}

output "key_vault_secret" {
  value = local.az.key_vault_secret
}

output "kubernetes_cluster" {
  value = local.az.kubernetes_cluster
}

output "lb" {
  value = local.az.lb
}

output "lb_nat_rule" {
  value = local.az.lb_nat_rule
}

output "linux_virtual_machine" {
  value = local.az.linux_virtual_machine
}

output "linux_virtual_machine_scale_set" {
  value = local.az.linux_virtual_machine_scale_set
}

output "local_network_gateway" {
  value = local.az.local_network_gateway
}

output "log_analytics_workspace" {
  value = local.az.log_analytics_workspace
}

output "managed_disk" {
  value = local.az.managed_disk
}

output "network_ddos_protection_plan" {
  value = local.az.network_ddos_protection_plan
}

output "network_interface" {
  value = local.az.network_interface
}

output "network_security_group" {
  value = local.az.network_security_group
}

output "network_security_group_rule" {
  value = local.az.network_security_group_rule
}

output "network_security_rule" {
  value = local.az.network_security_rule
}

output "network_watcher" {
  value = local.az.network_watcher
}

output "point_to_site_vpn_gateway" {
  value = local.az.point_to_site_vpn_gateway
}

output "private_dns_a_record" {
  value = local.az.private_dns_a_record
}

output "private_dns_aaaa_record" {
  value = local.az.private_dns_aaaa_record
}

output "private_dns_cname_record" {
  value = local.az.private_dns_cname_record
}

output "private_dns_mx_record" {
  value = local.az.private_dns_mx_record
}

output "private_dns_ptr_record" {
  value = local.az.private_dns_ptr_record
}

output "private_dns_srv_record" {
  value = local.az.private_dns_srv_record
}

output "private_dns_txt_record" {
  value = local.az.private_dns_txt_record
}

output "private_dns_zone" {
  value = local.az.private_dns_zone
}

output "private_dns_zone_group" {
  value = local.az.private_dns_zone_group
}

output "private_endpoint" {
  value = local.az.private_endpoint
}

output "private_link_service" {
  value = local.az.private_link_service
}

output "private_service_connection" {
  value = local.az.private_service_connection
}

output "proximity_placement_group" {
  value = local.az.proximity_placement_group
}

output "public_ip" {
  value = local.az.public_ip
}

output "public_ip_prefix" {
  value = local.az.public_ip_prefix
}

output "resource_group" {
  value = local.az.resource_group
}

output "role_assignment" {
  value = local.az.role_assignment
}

output "role_definition" {
  value = local.az.role_definition
}

output "route" {
  value = local.az.route
}

output "route_table" {
  value = local.az.route_table
}

output "shared_image" {
  value = local.az.shared_image
}

output "shared_image_gallery" {
  value = local.az.shared_image_gallery
}

output "snapshots" {
  value = local.az.snapshots
}

output "sql_elasticpool" {
  value = local.az.sql_elasticpool
}

output "sql_failover_group" {
  value = local.az.sql_failover_group
}

output "sql_firewall_rule" {
  value = local.az.sql_firewall_rule
}

output "sql_server" {
  value = local.az.sql_server
}

output "storage_account" {
  value = local.az.storage_account
}

output "storage_blob" {
  value = local.az.storage_blob
}

output "storage_container" {
  value = local.az.storage_container
}

output "storage_queue" {
  value = local.az.storage_queue
}

output "storage_share" {
  value = local.az.storage_share
}

output "storage_share_directory" {
  value = local.az.storage_share_directory
}

output "storage_table" {
  value = local.az.storage_table
}

output "subnet" {
  value = local.az.subnet
}

output "template_deployment" {
  value = local.az.template_deployment
}

output "traffic_manager_profile" {
  value = local.az.traffic_manager_profile
}

output "virtual_machine" {
  value = local.az.virtual_machine
}

output "virtual_machine_extension" {
  value = local.az.virtual_machine_extension
}

output "virtual_machine_scale_set" {
  value = local.az.virtual_machine_scale_set
}

output "virtual_machine_scale_set_extension" {
  value = local.az.virtual_machine_scale_set_extension
}

output "virtual_network" {
  value = local.az.virtual_network
}

output "virtual_network_gateway" {
  value = local.az.virtual_network_gateway
}

output "virtual_network_peering" {
  value = local.az.virtual_network_peering
}

output "virtual_wan" {
  value = local.az.virtual_wan
}

output "windows_virtual_machine" {
  value = local.az.windows_virtual_machine
}

output "windows_virtual_machine_scale_set" {
  value = local.az.windows_virtual_machine_scale_set
}


