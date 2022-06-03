# identifiers
variable "env" {
  type    = string
  default = "dev"
}

variable "region" {
  type    = string
  default = "uksouth"
}

variable "app" {
  type    = string
  default = "web"
}

variable "log_analytics_workspace_name" {
  type        = string
  default     = "log-plat-dev-shared"
}

variable "shared_resource_group_name" {
  type    = string
  default = "rg-sql-dev-shared"
}

variable "platform_shared_rg_name" {
  type    = string
  default = "rg-plat-dev-shared"
}

variable "sql_server_name_primary" {
  type    = string
  default = "sql-web-dev-uksouth"
}

variable "sql_server_name_secondary" {
  type    = string
  default = "sql-web-dev-ukwest"
}

variable "sql_ep_name" {
  type    = string
  default = "sqlep-web-dev-uksouth"
}

variable "sql_database_edition" {
  type    = string
  default = "Basic"
}

variable "sql_database_names" {
  type    = list(string)
  default = ["fieldsmart-dev"]
}

variable "mssql_database_extended_auditing_policy_storage_account_access_key_is_secondary" {
  type    = bool
  default = false
}

variable "mssql_database_extended_auditing_policy_retention_in_days" {
  type    = number
  default = 6
}

variable "mssql_database_threat_detection_policy_retention_in_days" {
  type    = number
  default = 6
}

variable "mssql_database_threat_detection_policy_email_addresses" {
  type    = list(string)
  default = ["harshkumar.bhalodia@capita.com"]
}

variable "mssql_database_threat_detection_policy_disabled_alerts" {
  type    = list(string)
  default = ["Access_Anomaly"]
}

variable "mssql_database_threat_detection_policy_state" {
  type    = string
  default = "Enabled"
}

variable "sa_sqldb_audit_diagnostics_logs" {
  type    = list(string)
  default = ["StorageWrite"]
}

variable "sa_sqldb_audit_diagnostics_metrics" {
  type    = list(string)
  default = ["all"]
}

variable "sa_sqltd_audit_diagnostics_logs" {
  type    = list(string)
  default = ["StorageWrite"]
}

variable "sa_sqltd_audit_diagnostics_metrics" {
  type    = list(string)
  default = ["all"]
}

variable "sqldb_audit_diagnostics_logs" {
  type    = list(string)
  default = ["SQLInsights","AutomaticTuning","QueryStoreRuntimeStatistics","QueryStoreWaitStatistics","Errors","DatabaseWaitStatistics","Timeouts","Blocks","Deadlocks"]
}

variable "sqldb_audit_diagnostics_metrics" {
  type    = list(string)
  default = ["Basic","InstanceAndAppAdvanced","WorkloadManagement"]
}
