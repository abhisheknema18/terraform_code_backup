resource "azurerm_mssql_elasticpool" "this" {
  name                = var.sql_elasticpool_name
  resource_group_name = var.resource_group_name
  location            = var.location
  server_name         = var.sql_server_name
  max_size_gb         = var.sql_elasticpool_max_size_gb
  tags                = var.tags
  
  sku {
    name     = var.sql_elasticpool_sku_name
    tier     = var.sql_elasticpool_edition
    capacity = var.sql_elasticpool_dtu
  }

  per_database_settings {
    min_capacity = var.sql_elasticpool_db_dtu_min
    max_capacity = var.sql_elasticpool_db_dtu_max
  }
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
  count                          = var.diagnostics != null ? 1 : 0
  name                           = var.diagnostics.name
  target_resource_id             = azurerm_mssql_elasticpool.this.id
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