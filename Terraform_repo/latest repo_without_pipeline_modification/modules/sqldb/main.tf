data "azurerm_storage_account" "storageaccount" {
  name                     = var.mssql_database_extended_auditing_policy_storage_account_name
  resource_group_name      = var.mssql_database_extended_auditing_policy_storage_account_resource_group_name
}

data "azurerm_storage_account" "storageaccount2" {
  name                     = var.mssql_database_threat_detection_policy_storage_account_name
  resource_group_name      = var.mssql_database_threat_detection_policy_storage_account_resource_group_name
}

resource "azurerm_mssql_database" "sqldatabase" {
  count               = length(var.sql_database_names)
  name                = var.sql_database_names[count.index]
  server_id           = var.sql_server_id
  elastic_pool_id     = var.sql_elasticpool_id
  tags                = var.tags
  threat_detection_policy {
    state                               = var.mssql_database_threat_detection_policy_state
    disabled_alerts                     = var.mssql_database_threat_detection_policy_disabled_alerts
    email_addresses                     = var.mssql_database_threat_detection_policy_email_addresses
    retention_days                      = var.mssql_database_threat_detection_policy_retention_in_days
    storage_endpoint                    = data.azurerm_storage_account.storageaccount2.primary_blob_endpoint
    storage_account_access_key          = data.azurerm_storage_account.storageaccount2.primary_access_key
  }
}

resource "azurerm_mssql_database_extended_auditing_policy" "mssqldatabaseextendedauditingpolicy" {
  count                                   = length(var.sql_database_names)
  database_id                             = azurerm_mssql_database.sqldatabase[count.index].id
  storage_endpoint                        = data.azurerm_storage_account.storageaccount.primary_blob_endpoint
  storage_account_access_key              = data.azurerm_storage_account.storageaccount.primary_access_key
  storage_account_access_key_is_secondary = var.mssql_database_extended_auditing_policy_storage_account_access_key_is_secondary
  retention_in_days                       = var.mssql_database_extended_auditing_policy_retention_in_days
}

locals {
  diag_resource_list = var.diagnostics != null ? split("/", var.diagnostics.destination) : []
  parsed_diag = var.diagnostics != null ? {
    log_analytics_id   = contains(local.diag_resource_list, "Microsoft.OperationalInsights") ? var.diagnostics.destination : null
    metric             = var.diagnostics.metrics
    log                = var.diagnostics.logs
  } : {
    log_analytics_id   = null
    metric             = []
    log                = []
  }
}
resource "azurerm_monitor_diagnostic_setting" "this" {
  count                          = var.diagnostics != null ? length(var.sql_database_names) : 0
  name                           = var.diagnostics.name
  target_resource_id             = azurerm_mssql_database.sqldatabase[count.index].id
  log_analytics_workspace_id     = local.parsed_diag.log_analytics_id

  dynamic "log" {
    for_each = var.diagnostics.logs
    content {
      category = log.value
      enabled  = contains(local.parsed_diag.log, "all") || contains(local.parsed_diag.log, log.value)

      retention_policy {
        enabled = false
        days    = 0
      }
    }
  }

  dynamic "metric" {
    for_each = var.diagnostics.metrics
    content {
      category = metric.value
      enabled  = contains(local.parsed_diag.metric, "all") || contains(local.parsed_diag.metric, metric.value)

      retention_policy {
        enabled = false
        days    = 0
      }
    }
  }
}
