resource "azurerm_traffic_manager_profile" "this" {

  max_return = var.max_return
  name = var.name
  profile_status = var.profile_status
  resource_group_name = var.resource_group_name
  traffic_routing_method = var.traffic_routing_method
  traffic_view_enabled = var.traffic_view_enabled

  dynamic "dns_config" {
    for_each = var.dns_config
    content {

      relative_name = dns_config.value["relative_name"]
      ttl = dns_config.value["ttl"]
    }
  }

  dynamic "monitor_config" {
    for_each = var.monitor_config
    content {
      expected_status_code_ranges = monitor_config.value["expected_status_code_ranges"]
      path = monitor_config.value["path"]
      port = monitor_config.value["port"]
      protocol = monitor_config.value["protocol"]
      timeout_in_seconds = monitor_config.value["timeout_in_seconds"]
      tolerated_number_of_failures = monitor_config.value["tolerated_number_of_failures"]
    }
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
  name                           = "${var.name}-tm-diag"
  target_resource_id             = azurerm_traffic_manager_profile.this.id
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
