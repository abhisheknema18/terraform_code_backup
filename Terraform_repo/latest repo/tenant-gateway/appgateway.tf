
module "application_gateway" {
  source = "../modules/appgateway/"


  resource_group_name       = azurerm_resource_group.app_gateway_rg.name
  location                  = azurerm_resource_group.app_gateway_rg.location
  name                      = module.naming_application_gateway.application_gateway.name
  enable_http2              = var.enable_http2
  firewall_policy_id        = var.firewall_policy_id
  sku                       = var.agw_sku
  frontend_port             = var.frontend_port
  backend_address_pool      = var.backend_address_pool
  probe                     = var.probe
  request_routing_rule      = var.request_routing_rule
  url_path_map              = var.url_path_map
  http_listener             = var.http_listener
  waf_configuration         = var.waf_configuration
  backend_http_settings     = var.backend_http_settings

  gateway_ip_configuration = [
    {
    name      = module.network.vnet_name
    subnet_id = module.network.vnet_subnets[0]

    }
  ]

  frontend_ip_configuration = [
    {
      name                 = azurerm_public_ip.this.name
      public_ip_address_id = azurerm_public_ip.this.id
    }
  ]

 ssl_certificate = [
    {
       data     = filebase64(var.ssl_certificate[0].data)
       name     = var.ssl_certificate[0].name
       password = var.ssl_certificate[0].password
     }
   ]

  diagnostics = {
    destination     = data.azurerm_log_analytics_workspace.platform.id
    logs            = var.appgateway_audit_diagnostics_logs
    metrics         = var.appgateway_audit_diagnostics_metrics
  }

}


