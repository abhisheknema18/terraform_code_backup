module "azurerm_traffic_manager_endpoint" {
  source = "../modules/trafficmanager/endpoint"

  name                  = "ep-${var.app}-${var.env}-${var.region}"
  profile_name          = module.naming_traffic_manager_profile.traffic_manager_profile.name
  resource_group_name   = azurerm_resource_group.app_gateway_rg.name
  target_resource_id    = azurerm_public_ip.this.id
  type                   = var.type

  depends_on = [module.application_gateway, module.azurerm_traffic_manager_profile]

}