module "sqlserver" {
  source                                  = "../modules/sqlserver"

  sql_server_name                         = module.naming.sql_server.name
  sql_server_version                      = var.sql_server_version
  sql_server_administrator_login          = var.sql_server_administrator_login
  sql_server_administrator_login_password = var.sql_server_administrator_login_password
  sql_public_network_access_enabled       = var.sql_public_network_access_enabled
  resource_group_name                     = azurerm_resource_group.global.name
  location                                = azurerm_resource_group.global.location

  sql_active_directory_administrator      = var.sql_active_directory_administrator 
  mssql_server_extended_auditing_policy_storage_account_name                    = module.sa_sql_audit.name
  mssql_server_extended_auditing_policy_storage_account_resource_group_name     = azurerm_resource_group.global.name
  mssql_server_extended_auditing_policy_storage_account_access_key_is_secondary = var.mssql_server_extended_auditing_policy_storage_account_access_key_is_secondary
  mssql_server_extended_auditing_policy_retention_in_days                       = var.mssql_server_extended_auditing_policy_retention_in_days

  sql_firewall_rule                       = var.sql_firewall_rule
  vulnerability_assessment_emails         = var.vulnerability_assessment_emails
}

module "sqlserver_sec" {
  source                                  = "../modules/sqlserver"

  sql_server_name                         = module.naming_sec.sql_server.name
  sql_server_version                      = var.sql_server_version
  sql_server_administrator_login          = var.sql_server_administrator_login
  sql_server_administrator_login_password = var.sql_server_administrator_login_password
  sql_public_network_access_enabled       = var.sql_public_network_access_enabled
  resource_group_name                     = azurerm_resource_group.global.name
  location                                = var.region_sec
  sql_active_directory_administrator      = var.sql_active_directory_administrator 

  mssql_server_extended_auditing_policy_storage_account_name                    = module.sa_sql_audit.name
  mssql_server_extended_auditing_policy_storage_account_resource_group_name     = azurerm_resource_group.global.name
  mssql_server_extended_auditing_policy_storage_account_access_key_is_secondary = var.mssql_server_extended_auditing_policy_storage_account_access_key_is_secondary
  mssql_server_extended_auditing_policy_retention_in_days                       = var.mssql_server_extended_auditing_policy_retention_in_days

  sql_firewall_rule                       = var.sql_firewall_rule
  vulnerability_assessment_emails         = var.vulnerability_assessment_emails
}


resource "azurerm_role_assignment" "vnet_permission_for_sql_to_storage_account" {
  scope                = data.azurerm_storage_account.storageaccount.id
  role_definition_name = "Contributor"
  principal_id         = module.sqlserver.identify.0.principal_id
   }
