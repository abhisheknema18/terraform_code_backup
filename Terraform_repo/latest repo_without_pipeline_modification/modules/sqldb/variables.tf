variable "sql_database_names" {
  type = list(string)
}
variable "sql_database_edition" {
  type = string
}
variable "mssql_database_extended_auditing_policy_storage_account_access_key_is_secondary" {
  type = bool
}
variable "mssql_database_extended_auditing_policy_retention_in_days" {
  type = number
}
variable "sql_server_id" {
  type = string
}
variable "sql_elasticpool_id" {
  type    = string
}
variable "tags" {
  type = map(string)
  default = {
    environment = "dev"
  }
}
variable "mssql_database_extended_auditing_policy_storage_account_name" {
  type = string
}
variable "mssql_database_extended_auditing_policy_storage_account_resource_group_name" {
  type = string
}

variable "mssql_database_threat_detection_policy_storage_account_name" {
  type = string
}
variable "mssql_database_threat_detection_policy_storage_account_resource_group_name" {
  type = string
}
variable "mssql_database_threat_detection_policy_retention_in_days" {
  type = number
}
variable "mssql_database_threat_detection_policy_email_addresses" {
  type = list(string)
}
variable "mssql_database_threat_detection_policy_disabled_alerts" {
  type = list(string)
}
variable "mssql_database_threat_detection_policy_state" {
  type = string
}
variable "diagnostics" {
  type = object({
    name          = string
    destination   = string
    logs          = list(string)
    metrics       = list(string)
  })
  default = null
}