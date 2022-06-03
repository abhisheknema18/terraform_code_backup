variable "resource_group_name" {
  type = string
}

variable "cluster_name" {
  type    = string
  default = null
}

variable "dns_prefix" {
  type    = string
  default = "dev"
}

variable "client_id" {
  type    = string
  default = ""
}

variable "client_secret" {
  type    = string
  default = ""
}

variable "admin_username" {
  default = "azureuser"
  type    = string
}

variable "agents_size" {
  default = "Standard_D2s_v3"
  type    = string
}


variable "agents_count" {
  type    = number
  default = 2
}

variable "public_ssh_key" {
  type    = string
  default = ""
}

variable "log_analytics_workspace_id" {
  type = string
}

variable "acr_id" {
  type = string
  default = null
}

variable "vnet_subnet_id" {
  type    = string
  default = null
}

variable "os_disk_size_gb" {
  type    = number
  default = 50
}

variable "private_cluster_enabled" {
  type    = bool
  default = true
}

variable "enable_kube_dashboard" {
  type    = bool
  default = false
}

variable "enable_azure_policy" {
  type    = bool
  default = false
}

variable "sku_tier" {
  type    = string
  default = "Free"
}

variable "enable_role_based_access_control" {
  type    = bool
  default = false
}

variable "network_plugin" {
  type    = string
  default = "kubenet"
}

variable "network_policy" {
  type    = string
  default = null
}

variable "net_profile_dns_service_ip" {
  type    = string
  default = null
}

variable "net_profile_docker_bridge_cidr" {
  type    = string
  default = null
}

variable "net_profile_outbound_type" {
  type    = string
  default = "userDefinedRouting"
}

variable "net_profile_pod_cidr" {
  type    = string
  default = null
}

variable "net_profile_service_cidr" {
  type    = string
  default = null
}

variable "kubernetes_version" {
  type    = string
  default = null
}

variable "orchestrator_version" {
  type    = string
  default = null
}

variable "enable_auto_scaling" {
  type    = bool
  default = false
}

variable "agents_max_count" {
  type    = number
  default = null
}

variable "agents_min_count" {
  type    = number
  default = null
}

variable "agents_pool_name" {
  type    = string
  default = "nodepool"
}

variable "enable_node_public_ip" {
  type    = bool
  default = false
}

variable "agents_availability_zones" {
  type    = list(string)
  default = null
}

variable "agents_labels" {
  type    = map(string)
  default = {}
}

variable "agents_type" {
  type    = string
  default = "VirtualMachineScaleSets"
}

variable "agents_tags" {
  type    = map(string)
  default = {}
}

variable "agents_max_pods" {
  type    = number
  default = null
}

variable "identity_type" {
  type    = string
  default = "SystemAssigned"
}

variable "user_assigned_identity_id" {
  type    = string
  default = null
}

variable "enable_host_encryption" {
  type    = bool
  default = false
}

variable "diagnostics" {
  type = object({
    destination   = string
    logs          = list(string)
    metrics       = list(string)
  })
  default = null
}
