
module "application_gateway_ukwest" {
  count                            = var.isMultiregion == true ? 1 : 0
  source = "../modules/appgateway/"


  resource_group_name       = azurerm_resource_group.app_gateway_ukwest[0].name
  location                  = azurerm_resource_group.app_gateway_ukwest[0].location
  name                      = module.naming_application_gateway_ukwest.application_gateway.name
  enable_http2              = var.enable_http2
  firewall_policy_id        = var.firewall_policy_id
  sku                       = var.agw_sku
  frontend_port             = var.frontend_port
  backend_address_pool      = var.backend_address_pool_ukwest
  probe                     = var.probe
  request_routing_rule      = var.request_routing_rule
  url_path_map              = var.url_path_map
  http_listener             = var.http_listener_ukwest
  waf_configuration         = var.waf_configuration
  backend_http_settings     = var.backend_http_settings

  gateway_ip_configuration = [
    {
    name      = module.network-ukwest[0].vnet_name
    subnet_id = module.network-ukwest[0].vnet_subnets[0]

    }
  ]

  frontend_ip_configuration = [
    {
      name                 = azurerm_public_ip.ukwest[0].name
      public_ip_address_id = azurerm_public_ip.ukwest[0].id
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
   depends_on = [null_resource.uksouthprovisioningflag]
}


