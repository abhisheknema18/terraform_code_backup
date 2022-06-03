# identifiers
variable "env" {
  type    = string
  default = "dev"
}

variable "region" {
  type    = string
  default = "uksouth"
}

variable "region_sec" {
  type    = string
  default = "ukwest"
}

variable "region_shared" {
  type    = string
  default = "shared"
}

variable "isMultiregion" {
  type    = bool
  default = false
}

variable "app" {
  type    = string
  default = "web"
}

variable "app_global" {
  type    = string
  default = "glb"
}

variable "log_analytics_workspace_name" {
  type        = string
  default     = "log-plat-dev-shared"
}

variable "aks_client_id" {
  type    = string
  default = ""
}

variable "aks_client_secret" {
  type    = string
  default = ""
}

variable "aks_kubernetes_version" {
  type    = string
  default = "1.21.7"
}

variable "aks_orchestrator_version" {
  type    = string
  default = "1.21.7"
}

variable "aks_network_plugin" {
  type    = string
  default = "azure"
}

variable "aks_os_disk_size_gb" {
  type    = number
  default = 50
}

variable "aks_sku_tier" {
  type    = string
  default = "Paid"
}

variable "aks_private_cluster_enabled" {
  type    = bool
  default = true
}

variable "aks_enable_azure_policy" {
  type    = bool
  default = true
}

variable "aks_enable_auto_scaling" {
  type    = bool
  default = true
}

variable "aks_enable_host_encryption" {
  type    = bool
  default = false
}

variable "aks_agents_min_count" {
  type    = number
  default = 2
}

variable "aks_agents_max_count" {
  type    = number
  default = 3
}

variable "aks_agents_count" {
  type    = number
  default = 2
}


variable "aks_agents_max_pods" {
  type    = number
  default = 100
}


variable "aks_agents_pool_name" {
  type    = string
  default = "exnodepool"
}


variable "aks_agents_type" {
  type    = string
  default = "VirtualMachineScaleSets"
}

variable "aks_enable_rbac" {
  type    = bool
  default = true
}

variable "aks_agents_availability_zones" {
  type    = list(string)
  default = ["1", "2"]
}

variable "aks_agents_labels_nodepool" {
  type    = string
  default = "defaultnodepool"
}

variable "aks_agents_tags_Agent" {
  type    = string
  default = "defaultnodepoolagent"
}

variable "aks_network_policy" {
  type    = string
  default = "azure"
}

variable "aks_net_profile_dns_service_ip" {
  type    = string
  default = "172.16.15.10"
}

variable "aks_net_profile_dns_service_ip_ukwest" {
  type    = string
  default = "10.16.15.10"
}

variable "aks_net_profile_docker_bridge_cidr" {
  type    = string
  default = "170.10.0.1/16"
}

variable "aks_net_profile_service_cidr" {
  type    = string
  default = "172.16.15.0/24"
}

variable "aks_net_profile_service_cidr_ukwest" {
  type    = string
  default = "10.16.15.0/24"
}

variable "network_address_spaces" {
  type    = list(string)
  default = ["172.16.8.0/21"]
}

variable "network_address_spaces_ukwest" {
  type    = list(string)
  default = ["10.16.8.0/21"]
}

variable "network_subnet_prefixes" {
  type    = list(string)
  default = ["172.16.8.0/22"]
}

variable "network_subnet_prefixes_ukwest" {
  type    = list(string)
  default = ["10.16.8.0/22"]
}

variable "network_subnet_names" {
  type    = list(string)
  default = ["snet-aks"]
}
variable "subnet_service_endpoints" {
  type    = map(list(string))
  default = {
    "snet-aks" = ["Microsoft.KeyVault","Microsoft.Storage"]
  }
}

variable "network_global_address_spaces" {
  type    = list(string)
  default = ["172.16.4.32/27"]
}

variable "network_global_subnet_prefixes" {
  type    = list(string)
  default = ["172.16.4.32/28"]
}

variable "network_global_subnet_names" {
  type    = list(string)
  default = ["snet-vm-sb"]
}

variable "network_subnet_enforce_private_link_endpoint_network_policies" {
  type    = map(bool)
  default = {
    "snet-aks" = true,
    "snet-vm-sb" = true
  }
}

variable "nsg_predefined_rules" {
  type    = any
  default = [
    {
      name     = "MSSQL"
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

variable "routetable_disable_bgp_route_propagation" {
  type    = bool
  default = false
}

variable "routetable_enable_force_tunneling" {
  type    = bool
  default = true
}

variable "acr_name" {
  type    = string
  default = "acrplatdevshared"
}

variable "platform_shared_rg_name" {
  type    = string
  default = "rg-plat-dev-shared"
}

variable "tag_behavior" {
  type    = string
  default = "overwrite"
}

variable "tag_nb_resources" {
  type    = number
  default = 1
}

variable "sa_file_share_audit_diagnostics_logs" {
  type    = list(string)
  default = ["StorageWrite"]
}

variable "sa_file_share_audit_diagnostics_metrics" {
  type    = list(string)
  default = ["all"]
}

variable "kv_audit_diagnostics_logs" {
  type    = list(string)
  default = ["AuditEvent","AzurePolicyEvaluationDetails"]
}

variable "kv_audit_diagnostics_metrics" {
  type    = list(string)
  default = ["AllMetrics"]
}

variable "aks_audit_diagnostics_logs" {
  type    = list(string)
  default = ["kube-apiserver","kube-audit","kube-audit-admin","kube-controller-manager","kube-scheduler","cluster-autoscaler","cloud-controller-manager","guard"]
}

variable "aks_audit_diagnostics_metrics" {
  type    = list(string)
  default = ["AllMetrics"]
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

variable "vm_is_windows_image" {
  type    = bool
  default = true
}

variable "vm_hostname" {
  type    = string
  default = "sb01winsrvuks"
}

variable "vm_admin_password" {
  type    = string
  default = "T6&$aiuUL345&kl)0"
}

variable "vm_os_simple" {
  type    = string
  default = "WindowsServer"
}

variable "vm_number_of_public_ip" {
  type    = number
  default = 0
}

variable "kv_sku" {
  type    = string
  default = "standard"
}

variable "kv_soft_delete_retention_days" {
  type    = number
  default = 31
}

variable "kv_tags" {
  type = map(string)
  default = {}
}

variable "kv_secrets_map" {
  type = map(string)
  default = {
    "DbUser"= "DevLogin"
    "DbPassword"= "FieldsmartMobileDev@123"
    "AuthAudience" = "tn47mNO4J4gySXDG3bhuc11miYI5Jgnh"
    "InstanceKey" = "hiddensercretkey"
    "JwtIssuerUri" = "https://mcamt.eu.auth0.com/"
  }
}

variable "kv_secret_private_key" {
  type    = string
  default = "privatePKCS8"
}

variable "kv_secret_public_key" {
  type    = string
  default = "public"
}

variable "kv_key_permissions_read" {
  type        = list(string)
  default     = [ "get", "list" ]
}

variable "kv_secret_permissions_read" {
  type        = list(string)
  default     = [ "get", "list" ]
}

variable "kv_certificate_permissions_read" {
  type        = list(string)
  default     = [ "get", "getissuers", "list", "listissuers" ]
}

variable "kv_storage_permissions_no_access" {
  type = list(string)
  default = []
}

variable "fs_name" {
  type    = string
  default = "fieldsmart"
}

variable "fs_quota" {
  type = number
  default = 2048
}

variable "fs_primary_directories" {
  description = "(optional)"
  type        = list(string)
  default = ["configs", "fea", "logs", "packages", "results", "scripts", "sessions", "temp", "transactions", "workorders"]
}

variable "fs_secondary_directories" {
  default = ["logs/auditdb", "results/archive", "results/error", "results/pickup", "results/templates", "scripts/reference", 
    "scripts/source", "scripts/support", "temp/filedownloads", "temp/fileuploads"]
}

variable "hub_vnet_name" {
  type    = string
  default = "vnet-hub-dev-shared"
}

variable "hub_vnet_resource_group" {
  type    = string
  default = "rg-hub-dev-shared"
}

variable "routetable_next_hop_type" {
  type    = string
  default = "VirtualAppliance"
}

variable "routetable_next_hop_in_ip_address" {
  type    = string
  default = "192.168.42.132"
}

variable "tenant_primary_sqlserver_name" {
  type    = string
  default = "sql-web-dev-uksouth"
}

variable "tenant_sqlserver_resource_group" {
  type    = string
  default = "rg-sql-dev-shared"
}

variable "azurerm_private_dns_zone_sql_name" {
  type    = string
  default = "privatelink.database.windows.net"
}

variable "azurerm_private_dns_zone_acr_name" {
  type    = string
  default = "privatelink.azurecr.io"
}


variable "platform_operationvm_subnet_name" {
  type    = string
  default = "snet-ops"
}

variable "platform_adovm_subnet_name" {
  type    = string
  default = "snet-vm-ado"
}

variable "platform_operationvm_vnet_name" {
  type    = string
  default = "vnet-plat-dev-shared"
}
