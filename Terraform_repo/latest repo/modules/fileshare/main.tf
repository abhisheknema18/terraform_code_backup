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

resource "random_string" "unique" {
  length  = 6
  special = false
  upper   = false
}

resource "azurerm_storage_account" "file_share_sa" {
  name                      = format("%s%s", lower(replace(var.name, "/[[:^alnum:]]/", "")), random_string.unique.result)
  resource_group_name       = var.resource_group_name
  location                  = var.location
  account_kind              = "StorageV2"
  account_tier              = var.account_tier
  account_replication_type  = var.account_replication_type
  access_tier               = var.access_tier
  enable_https_traffic_only = true
  min_tls_version           = var.min_tls_version

  dynamic "network_rules" {
    for_each = var.network_rules
    content {
      bypass                     = network_rules.value["bypass"]
      default_action             = network_rules.value["default_action"]
      ip_rules                    = network_rules.value["ip_rules"]
      virtual_network_subnet_ids = network_rules.value["virtual_network_subnet_ids"]
    }
  }
}

resource "azurerm_advanced_threat_protection" "threat_protection" {
  count              = var.enable_advanced_threat_protection ? 1 : 0
  target_resource_id = azurerm_storage_account.file_share_sa.id
  enabled            = var.enable_advanced_threat_protection
}

data "azurerm_monitor_diagnostic_categories" "this" {
  resource_id = "${azurerm_storage_account.file_share_sa.id}/fileServices/default"
}

resource "azurerm_monitor_diagnostic_setting" "this" {
  count                          = var.diagnostics != null ? 1 : 0
  name                           = "${var.name}-sa-diag"
  target_resource_id             = "${azurerm_storage_account.file_share_sa.id}/fileServices/default"
  log_analytics_workspace_id     = local.parsed_diag.log_analytics_id

  dynamic "log" {
    for_each = data.azurerm_monitor_diagnostic_categories.this.logs
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
    for_each = data.azurerm_monitor_diagnostic_categories.this.metrics
    content {
      category = metric.value
      enabled  = contains(local.parsed_diag.metric, "all") || contains(local.parsed_diag.metric, metric.value)

      retention_policy {
        enabled = false
        days    = 0
      }
    }
  }

  lifecycle {
    ignore_changes = [
      log,
      metric
    ]
  }
}

resource "azurerm_storage_share" "this" {
  name  = var.file_share_name
  quota = var.quota
  storage_account_name = azurerm_storage_account.file_share_sa.name
}

resource "azurerm_storage_share_directory" "primary" {
  count                   = length(var.primary_directories)
  name                    = var.primary_directories[count.index]
  metadata                = var.primary_metadata
  share_name              = azurerm_storage_share.this.name
  storage_account_name    = azurerm_storage_account.file_share_sa.name
}

resource "azurerm_storage_share_directory" "secondary" {
  count                   = length(var.secondary_directories)
  name                    = var.secondary_directories[count.index]
  metadata                = var.secondary_metadata
  share_name              = azurerm_storage_share.this.name
  storage_account_name    = azurerm_storage_account.file_share_sa.name
  
  depends_on              = [azurerm_storage_share_directory.primary]
}

resource "azurerm_monitor_diagnostic_setting" "storage_account_dig_setting" {
  count                      = var.diagnostics != null ? 1 : 0
  name                       = "${format("%s%s", lower(replace(var.name, "/[[:^alnum:]]/", "")), random_string.unique.result)}-diag"
  log_analytics_workspace_id = local.parsed_diag.log_analytics_id
  target_resource_id         = azurerm_storage_account.file_share_sa.id
  metric { category = "Transaction"}
  lifecycle {
    ignore_changes = [
      metric
    ]
  }
}