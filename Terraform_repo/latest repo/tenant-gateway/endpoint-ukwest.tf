module "azurerm_traffic_manager_endpoint_ukwest" {
  count                            = var.isMultiregion == true ? 1 : 0
  source = "../modules/trafficmanager/endpoint"

  name                  = "ep-${var.app}-${var.env}-${var.region_sec}"
  profile_name          = module.naming_traffic_manager_profile.traffic_manager_profile.name
  resource_group_name   = azurerm_resource_group.app_gateway_rg.name
  target_resource_id    = azurerm_public_ip.ukwest[0].id
  type                   = var.type

  depends_on = [null_resource.uksouthprovisioningflag, module.application_gateway_ukwest, module.azurerm_traffic_manager_profile]

}