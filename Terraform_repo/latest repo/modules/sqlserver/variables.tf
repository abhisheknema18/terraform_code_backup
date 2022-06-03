
variable "sql_server_name" {
  type = string
}
variable "tags" {
  type = map(string)
  default = {
    environment = "dev"
  }
}
variable "sql_server_version" {
  type = string
  default = "12.0"
}
variable "sql_server_administrator_login" {
  type = string
}
variable "sql_server_administrator_login_password" {
  type = string
}
variable "sql_public_network_access_enabled" {
  type = string
}
variable "resource_group_name" {
  type = string
}
variable "location" {
  type = string
}
variable "sql_firewall_rule" {
  type = any
  default = []
}
variable "sql_virtual_network_rule" {
  type = any
  default = []
}
variable "sql_active_directory_administrator" {
  type = any
  default = []
}

variable "mssql_server_extended_auditing_policy_storage_account_access_key_is_secondary" {
  type = bool
}
variable "mssql_server_extended_auditing_policy_retention_in_days" {
  type = number
}
variable "mssql_server_extended_auditing_policy_storage_account_name" {
  type = string
}
variable "mssql_server_extended_auditing_policy_storage_account_resource_group_name" {
  type = string
}
variable "vulnerability_assessment_emails" {
  type = list(string)
}