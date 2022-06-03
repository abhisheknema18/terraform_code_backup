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

resource "azurerm_storage_account" "this" {
  name                      = format("%s%s", lower(replace(var.name, "/[[:^alnum:]]/", "")), random_string.unique.result)
  resource_group_name       = var.resource_group_name
  location                  = var.location
  account_kind              = "StorageV2"
  account_tier              = var.account_tier
  account_replication_type  = var.account_replication_type
  access_tier               = var.access_tier
  enable_https_traffic_only = true
  min_tls_version           = var.min_tls_version

  blob_properties {
    delete_retention_policy {
      days = var.soft_delete_retention
    }
    dynamic "cors_rule" {
      for_each = var.cors_rule
      content {
        allowed_origins    = cors_rule.value.allowed_origins
        allowed_methods    = cors_rule.value.allowed_methods
        allowed_headers    = cors_rule.value.allowed_headers
        exposed_headers    = cors_rule.value.exposed_headers
        max_age_in_seconds = cors_rule.value.max_age_in_seconds
      }
    }
  }

  dynamic "network_rules" {
    for_each = var.network_rules != null ? ["true"] : []
    content {
      default_action             = "Deny"
      ip_rules                   = var.network_rules.ip_rules
      virtual_network_subnet_ids = var.network_rules.subnet_ids
      bypass                     = var.network_rules.bypass
    }
  }
}

resource "azurerm_advanced_threat_protection" "threat_protection" {
  count              = var.enable_advanced_threat_protection ? 1 : 0
  target_resource_id = azurerm_storage_account.this.id
  enabled            = var.enable_advanced_threat_protection
}

data "azurerm_monitor_diagnostic_categories" "this" {
  resource_id = "${azurerm_storage_account.this.id}/blobServices/default"
}

resource "azurerm_monitor_diagnostic_setting" "this" {
  count                          = var.diagnostics != null ? 1 : 0
  name                           = "${var.name}-sa-diag"
  target_resource_id             = "${azurerm_storage_account.this.id}/blobServices/default"
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
}
resource "azurerm_monitor_diagnostic_setting" "storage_account_dig_setting" {
  count                      = var.diagnostics != null ? 1 : 0
  name                       = "${format("%s%s", lower(replace(var.name, "/[[:^alnum:]]/", "")), random_string.unique.result)}-diag"
  log_analytics_workspace_id = local.parsed_diag.log_analytics_id
  target_resource_id         = azurerm_storage_account.this.id
  metric { category = "Transaction"}
  lifecycle {
    ignore_changes = [
      log,
      metric
    ]
  }
}