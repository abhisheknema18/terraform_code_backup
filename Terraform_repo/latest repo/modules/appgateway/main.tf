resource "azurerm_application_gateway" "this" {
  name                 = var.name
  resource_group_name  = var.resource_group_name
  enable_http2         = var.enable_http2
  firewall_policy_id   = var.firewall_policy_id
  location             = var.location
  tags                 = var.tags
  zones                = var.zones

  dynamic "authentication_certificate" {
    for_each = var.authentication_certificate
    content {

      data = authentication_certificate.value["data"]
      name = authentication_certificate.value["name"]
    }
  }

  dynamic "autoscale_configuration" {
    for_each = var.autoscale_configuration
    content {
      # max_capacity - (optional) is a type of number
      max_capacity = autoscale_configuration.value["max_capacity"]
      # min_capacity - (required) is a type of number
      min_capacity = autoscale_configuration.value["min_capacity"]
    }
  }

  dynamic "backend_address_pool" {
    for_each = var.backend_address_pool
    content {
      fqdns = backend_address_pool.value["fqdns"]
      ip_addresses = backend_address_pool.value["ip_addresses"]
      name = backend_address_pool.value["name"]
    }
  }


  dynamic "backend_http_settings" {
    for_each = var.backend_http_settings
    content {
      cookie_based_affinity = backend_http_settings.value["cookie_based_affinity"]
      host_name = backend_http_settings.value["host_name"]
      name = backend_http_settings.value["name"]
      path = backend_http_settings.value["path"]
      pick_host_name_from_backend_address = backend_http_settings.value["pick_host_name_from_backend_address"]
      port = backend_http_settings.value["port"]
      probe_name = backend_http_settings.value["probe_name"]
      protocol = backend_http_settings.value["protocol"]
      request_timeout = backend_http_settings.value["request_timeout"]

    }
  }

  dynamic "frontend_ip_configuration" {
    for_each = var.frontend_ip_configuration
    content {
      name = frontend_ip_configuration.value["name"]
      public_ip_address_id = frontend_ip_configuration.value["public_ip_address_id"]

    }
  }

  dynamic "frontend_port" {
    for_each = var.frontend_port
    content {
      name = frontend_port.value["name"]
      port = frontend_port.value["port"]
    }
  }

  dynamic "gateway_ip_configuration" {
    for_each = var.gateway_ip_configuration
    content {

      name = gateway_ip_configuration.value["name"]
      subnet_id = gateway_ip_configuration.value["subnet_id"]

    }
  }

  dynamic "http_listener" {
    for_each = var.http_listener
    content {

      frontend_ip_configuration_name = http_listener.value["frontend_ip_configuration_name"]
      frontend_port_name = http_listener.value["frontend_port_name"]
      host_name = http_listener.value["host_name"]
      name = http_listener.value["name"]
      protocol = http_listener.value["protocol"]
      require_sni = http_listener.value["require_sni"]
      ssl_certificate_name = http_listener.value["ssl_certificate_name"]

    }
  }
    dynamic "probe" {
    for_each = var.probe
    content {

      host = probe.value["host"]
      interval = probe.value["interval"]
      name = probe.value["name"]
      path = probe.value["path"]
      pick_host_name_from_backend_http_settings = probe.value["pick_host_name_from_backend_http_settings"]
      port = probe.value["port"]
      protocol = probe.value["protocol"]
      timeout = probe.value["timeout"]
      unhealthy_threshold = probe.value["unhealthy_threshold"]
    }
  }


   dynamic "request_routing_rule" {
    for_each = var.request_routing_rule
    content {

      backend_address_pool_name = request_routing_rule.value["backend_address_pool_name"]
      backend_http_settings_name = request_routing_rule.value["backend_http_settings_name"]
      http_listener_name = request_routing_rule.value["http_listener_name"]
      name = request_routing_rule.value["name"]
      rule_type = request_routing_rule.value["rule_type"]
      url_path_map_name = request_routing_rule.value["url_path_map_name"]
    }
  }


  dynamic "sku" {
    for_each = var.sku
    content {
      capacity = sku.value["capacity"]
      name = sku.value["name"]
      tier = sku.value["tier"]
    }
  }

  dynamic "ssl_certificate" {
    for_each = var.ssl_certificate
    content {

      data = ssl_certificate.value["data"]
      name = ssl_certificate.value["name"]
      password = ssl_certificate.value["password"]
    }
  }


  dynamic "url_path_map" {
    for_each = var.url_path_map
    content {

      default_backend_address_pool_name = url_path_map.value["default_backend_address_pool_name"]
      default_backend_http_settings_name = url_path_map.value["default_backend_http_settings_name"]
      name = url_path_map.value["name"]

      dynamic "path_rule" {
        for_each = url_path_map.value.path_rule
        content {

          backend_address_pool_name = path_rule.value["backend_address_pool_name"]
          backend_http_settings_name = path_rule.value["backend_http_settings_name"]
          name = path_rule.value["name"]
          paths = path_rule.value["paths"]
        }
      }

    }
  }

  dynamic "waf_configuration" {
    for_each = var.waf_configuration
    content {

      enabled = waf_configuration.value["enabled"]
      firewall_mode = waf_configuration.value["firewall_mode"]
      rule_set_type = waf_configuration.value["rule_set_type"]
      rule_set_version = waf_configuration.value["rule_set_version"]

      dynamic "disabled_rule_group" {
        for_each = waf_configuration.value.disabled_rule_group
        content {
          rule_group_name = disabled_rule_group.value["rule_group_name"]
          rules = disabled_rule_group.value["rules"]
        }
      }

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
  name                           = "${var.name}-appgateway-diag"
  target_resource_id             = azurerm_application_gateway.this.id
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
