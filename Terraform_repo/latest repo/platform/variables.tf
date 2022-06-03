# identifiers
variable "env" {
  type    = string
  default = "dev"
}

variable "region" {
  type    = string
  default = "uksouth"
}
variable "region_shared" {
  type    = string
  default = "shared"

}
variable "app" {
  type    = string
  default = "plat"
}
variable "log_analytics_workspace_plan_publisher" {
  type    = string
  default = "Microsoft"
}

variable "log_analytics_workspace_plan_product" {
  type    = string
  default = "OMSGallery/ContainerInsights"
}

variable "log_analytics_workspace_count" {
  type    = number
  default = 1
}

variable "log_analytics_solution_name" {
  type    = string
  default = "ContainerInsights"
}

variable "log_analytics_workspace_sku" {
  type    = string
  default = "PerGB2018"
}

variable "log_retention_in_days" {
  type    = number
  default = 30
}

variable "public_network_access_enabled" {
  type    = bool
  default = false
}

variable "network_address_spaces" {
  type = list(string)
  default = ["172.16.3.0/24"]
}

variable "network_subnet_prefixes" {
  type = list(string)
  default = ["172.16.3.32/27", "172.16.3.64/27", "172.16.3.0/28", "172.16.3.16/28"]
}

variable "network_subnet_names" {
  type = list(string)
  default = ["snet-mntr", "snet-acr", "snet-vm-ado", "snet-ops"]
}

variable "network_subnet_enforce_private_link_endpoint_network_policies" {
  type = map(bool)
  default = {
    "snet-acr" = true
    "snet-ops" = true
    "snet-vm-ado" = true
  }
}
variable "subnet_service_endpoints" {
  type    = map(list(string))
  default = {
    "snet-vm-ado" = ["Microsoft.KeyVault","Microsoft.Storage"]
    "snet-ops" = ["Microsoft.KeyVault","Microsoft.Storage"]

  }
}

variable "routetable_disable_bgp_route_propagation" {
  type    = bool
  default = false
}
variable "routetable_enable_force_tunneling" {
  type    = bool
  default = true
}
variable "routetable_next_hop_type" {
  type    = string
  default = "VirtualAppliance"
}
variable "routetable_next_hop_in_ip_address" {
  type = string
  default = "192.168.42.132"
}
variable "virtual_network_peering_platform_to_firewall_name" {
  type = string
  default = "PlatformToFirewall"
}
variable "virtual_network_peering_firewall_to_platform" {
  type = string
  default = "FirewallToPlatform"
}
variable "log_analytics_workspace_name" {
  type = string
  default = ""
}

variable "hub_vnet_resource_group" {
  type    = string
  default = "rg-hub-dev-shared"
}
variable "enable_force_tunneling" {
  type    = bool
  default = false
}

variable "hub_vnet_name" {
  type    = string
  default = "vnet-hub-dev-shared"
}

variable "data_disk" {
  type = bool
  default = false
}

variable "linux_vm_hostname" {
  type = string
  default = "ado-lin-uks"
}

variable "windows_vm_hostname" {
  type = string
  default = "ops-win-uks"
}

variable "vm_admin_password" {
  type    = string
  default = "T6&$aiuUL345&kl)0"
}

variable "linux_vm_os_simple" {
  type    = string
  default = "UbuntuServer"
}

variable "window_vm_os_simple" {
  type    = string
  default = "Windows-10"
}

variable "ado_build_agent_vm_nb_instances" {
  type = number
  default = 2
}

variable "ado_deploy_agent_vm_nb_instances" {
  type = number
  default = 1
}

variable "windows_nb_instances" {
  type = number
  default = 1
}


variable "vm_os_id" {
  type = string
  default = ""
}

###################################################################



variable "public_ip_dns" {
  type        = list(string)
  default     = [null]
}



variable "extra_ssh_keys" {
  type        = list(string)
  default     = []
}

variable "ssh_key" {
  type        = string
  default     = "~/.ssh/id_rsa.pub"
}

variable "ssh_key_values" {
  type        = list(string)
  default     = []
}
variable "remote_port" {
  type        = string
  default     = ""
}

variable "admin_username" {
  type        = string
  default     = "azureuser"
}

variable "custom_data" {
  type        = string
  default     = ""
}

variable "storage_account_type" {
  type        = string
  default     = "Premium_LRS"
}

variable "vm_size" {
  type        = string
  default     = "Standard_D2s_v3"
}


variable "is_windows_image" {
  type        = bool
  default     = false
}


variable "vm_os_version" {
  type        = string
  default     = "latest"
}

variable "tags" {
  type        = map(string)

  default = {
    source = "terraform"
  }
}

variable "allocation_method" {
  type        = string
  default     = "Dynamic"
}

variable "public_ip_sku" {
  type        = string
  default     = "Basic"
}

variable "nb_public_ip" {
  type        = number
  default     = 0
}

variable "delete_os_disk_on_termination" {
  type        = bool
  default     = true
}

variable "delete_data_disks_on_termination" {
  type        = bool
  default     = true
}

variable "data_sa_type" {
  type        = string
  default     = "Premium_LRS"
}

variable "data_disk_size_gb" {
  type        = number
  default     = 128
}

variable "boot_diagnostics" {
  type        = bool
  default     = false
}

variable "boot_diagnostics_sa_type" {
  type        = string
  default     = "Standard_LRS"
}

variable "enable_accelerated_networking" {
  type        = bool
  default     = false
}

variable "enable_ssh_key" {
  type        = bool
  default     = true
}

variable "nb_data_disk" {
  type        = number
  default     = 0
}
variable "nb_data_disk_ado" {
  type        = number
  default     = 1
}


variable "license_type" {
  type        = string
  default     = null
}

variable "identity_type" {
  type        = string
  default     = ""
}

variable "identity_ids" {
  type        = list(string)
  default     = []
}

variable "extra_disks" {
  type = list(object({
    name = string
    size = number
  }))
  default = []
}

variable "os_profile_secrets" {
  type        = list(map(string))
  default     = []
}

variable "nsg_predefined_rules_ado" {
  type    = any
  default = [
    {
      name     = "SSH"
      priority = "200"
    }
  ]
}

variable "nsg_predefined_rules_ops" {
  type    = any
  default = [
     {
      name     = "RDP"
      priority = "200"
    }
  ]
}
variable "nsg_tags" {
  type = map(string)
  default = {
    environment = "dev"
    costcenter  = "it"
  }
}

variable "public_ssh_key" {
  type    = string
  default = ""
}

variable "ssh_private_key_filename" {
  type = string
  default = "./private_ssh_key"
}

variable "ssh_public_key_filename" {
  type = string
  default = ""
}

variable "ssh_integration_service_public_key_filename" {
  type = string
  default = "./public_int_ssh_key.pem"
}

variable "ssh_integration_service_private_key_filename" {
  type = string
  default = "./private_int_ssh_key.pem"
}

variable "ssh_integration_service_pkcs8_private_key_filename" {
  type = string
  default = "privatePKCS8.pem"
}

variable "hub_subscription_id" {
  type = string
  default = "a95faac2-cab9-4d56-8c6d-fc818f9cf136"
}
#########################################################


variable "managed" {
description = "(optional)"
type        = bool
default     = true
}

variable "availability_set_ado_name" {
type        = string
default     = ""
}

variable "availability_set_ops_name" {
  type        = string
  default = ""
}

variable "platform_fault_domain_count" {
description = "(optional)"
type        = number
default     = 2
}

variable "platform_update_domain_count" {
description = "(optional)"
type        = number
default     = 2
}

variable "availability_set_id" {
  type = string
  default = ""
}


variable "timeouts" {
description = "nested block: NestingSingle, min items: 0, max items: 0"
type = set(object(
{
create = string
delete = string
read   = string
update = string
}
))
default = []
}


variable "vm_audit_diagnostics_logs" {
  type    = list(string)
  default = []
}

variable "vm_audit_diagnostics_metrics" {
  type    = list(string)
  default = ["AllMetrics"]
}

variable "firewall_name" {
  type = string
  default = ""
}

variable "azurerm_private_dns_zone_acr_name" {
  type    = string
  default = "privatelink.azurecr.io"
}

variable "IaaSAntimalware_type_handler_version" {
  type    = string
  default = "1.3"
}
