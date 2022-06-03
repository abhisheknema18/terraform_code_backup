module "sqldatabase" {
  source  = "../modules/sqldb"

  sql_server_id               = data.azurerm_mssql_server.primary.id
  sql_elasticpool_id          = data.azurerm_mssql_elasticpool.primary.id
  sql_database_names          = var.sql_database_names
  sql_database_edition        = var.sql_database_edition

  mssql_database_extended_auditing_policy_storage_account_name                    = module.sa_sqldb_audit.name
  mssql_database_extended_auditing_policy_storage_account_resource_group_name     = var.shared_resource_group_name
  mssql_database_extended_auditing_policy_storage_account_access_key_is_secondary = var.mssql_database_extended_auditing_policy_storage_account_access_key_is_secondary
  mssql_database_extended_auditing_policy_retention_in_days                       = var.mssql_database_extended_auditing_policy_retention_in_days

  mssql_database_threat_detection_policy_storage_account_name                     = module.sa_sqltd_audit.name
  mssql_database_threat_detection_policy_storage_account_resource_group_name      = var.shared_resource_group_name
  mssql_database_threat_detection_policy_retention_in_days                        = var.mssql_database_threat_detection_policy_retention_in_days
  mssql_database_threat_detection_policy_email_addresses                          = var.mssql_database_threat_detection_policy_email_addresses
  mssql_database_threat_detection_policy_disabled_alerts                          = var.mssql_database_threat_detection_policy_disabled_alerts
  mssql_database_threat_detection_policy_state                                    = var.mssql_database_threat_detection_policy_state
  diagnostics = {
    name            = "primary-sqldb-diag-setting"
    destination     = data.azurerm_log_analytics_workspace.platform.id
    logs            = var.sqldb_audit_diagnostics_logs
    metrics         = var.sqldb_audit_diagnostics_metrics
  }
}
resource "azurerm_mssql_database" "secondary" {
  count                         = length(var.sql_database_names)
  name                          = var.sql_database_names[count.index]
  server_id                     = data.azurerm_mssql_server.sec.id
  create_mode                   = "Secondary"
  creation_source_database_id   = module.sqldatabase.id[count.index]
  elastic_pool_id               = data.azurerm_mssql_elasticpool.sec.id
}

resource "azurerm_monitor_diagnostic_setting" "secondary_db_diag" {
  name = "secondary-sqldb-diag-setting"
  log_analytics_workspace_id = data.azurerm_log_analytics_workspace.platform.id
  target_resource_id = azurerm_mssql_database.secondary[0].id
  log  {category = "SQLInsights"}
  log {category = "AutomaticTuning"}
  log {category = "QueryStoreRuntimeStatistics"}
  log  {category = "QueryStoreWaitStatistics"}
  log {category = "Errors"}
  log {category = "DatabaseWaitStatistics"}
  log  {category = "Timeouts"}
  log {category = "Blocks"}
  log {category = "Deadlocks"}
  metric { category = "Basic"}
  metric { category = "InstanceAndAppAdvanced"}
  metric { category = "WorkloadManagement"}
  depends_on = [azurerm_mssql_database.secondary]
}