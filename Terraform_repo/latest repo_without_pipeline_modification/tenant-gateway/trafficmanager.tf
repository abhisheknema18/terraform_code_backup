module "azurerm_traffic_manager_profile" {
  source = "../modules/trafficmanager/profile"


  max_return = null
  name                   = module.naming_traffic_manager_profile.traffic_manager_profile.name
  resource_group_name    = azurerm_resource_group.app_gateway_rg.name
  profile_status         = var.profile_status
  traffic_routing_method = var.traffic_routing_method
  traffic_view_enabled   = var.traffic_view_enabled
  dns_config = [
    {
      relative_name = module.naming_traffic_manager_profile.traffic_manager_profile.name
      ttl           = var.dns_config[0].ttl
    }
  ]

  monitor_config = var.monitor_config
  diagnostics = {
    destination     = data.azurerm_log_analytics_workspace.platform.id
    logs            = var.traffic_manager_audit_diagnostics_logs
    metrics         = var.traffic_manager_diagnostics_metrics
  }

}

