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

variable "app" {
  type    = string
  default = "web"
}

variable "app_type" {
  type    = string
  default = "sql"
}

variable "log_analytics_workspace_name" {
  type        = string
  default     = "log-plat-dev-shared"
}

variable "platform_shared_rg_name" {
  type    = string
  default = "rg-plat-dev-shared"
}

variable "ep_sql_elasticpool_max_size_gb" {
  type    = number
  default = 4.8828125
}

variable "ep_sql_elasticpool_dtu" {
  type    = number
  default = 50
}

variable "ep_sql_elasticpool_edition" {
  type    = string
  default = "Basic"
}

variable "ep_sql_elasticpool_db_dtu_min" {
  type    = number
  default = 0
}

variable "ep_sql_elasticpool_db_dtu_max" {
  type    = number
  default = 5
}

variable "ep_sql_elasticpool_sku_name" {
  type    = string
  default = "BasicPool"
}

variable "sql_server_version" {
  type    = string
  default = "12.0"
}

variable "sql_server_administrator_login" {
  type    = string
  default = "fieldsmartadmin"
}

variable "sql_server_administrator_login_password" {
  type    = string
  default = "Fieldsmart@123Fieldsmart@123"
}

variable "sql_public_network_access_enabled" {
  type    = bool
  default = false
}

variable "sql_active_directory_administrator" {
  type = any
  default = [
    {
      "user_name" : "harsh",
      "login"     : "sqladmin",
      "object_id" : "13695fbc-1a93-498e-a3e7-478a369e4f6a"
    }
  ]
}

variable "sql_firewall_rule" {
  type = any
  default = [
    {
      "name" : "PublicAccess",
      "start_ip_address"     : "0.0.0.0",
      "end_ip_address" : "255.255.255.255"
    }
  ]
}

variable "mssql_server_extended_auditing_policy_storage_account_access_key_is_secondary" {
  type    = bool
  default = false
}

variable "mssql_server_extended_auditing_policy_retention_in_days" {
  type    = number
  default = 6
}

variable "sa_sql_audit_diagnostics_logs" {
  type    = list(string)
  default = ["StorageWrite"]
}

variable "sa_sql_audit_diagnostics_metrics" {
  type    = list(string)
  default = ["all"]
}
variable "elasticpool_audit_diagnostics_logs" {
  type    = list(string)
  default = []
}

variable "elasticpool_audit_diagnostics_metrics" {
  type    = list(string)
  default = ["InstanceAndAppAdvanced", "Basic"]
}

variable "azurerm_private_dns_zone_sql_name" {
  type    = string
  default = "privatelink.database.windows.net"
}

variable "platform_operationvm_subnet_name" {
  type    = string
  default = "snet-ops"
}

variable "platform_operationvm_vnet_name" {
  type    = string
  default = "vnet-plat-dev-shared"
}

variable "vulnerability_assessment_emails" {
  type = list(string)
  default = [
      "harshkumar.bhalodia@capita.com",
      "abhishek.nema@capita.com"
    ]
}

variable "platform_adovm_subnet_name" {
  type    = string
  default = "snet-vm-ado"
}
