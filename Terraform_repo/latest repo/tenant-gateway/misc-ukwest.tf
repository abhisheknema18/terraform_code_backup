resource "null_resource" "uksouthprovisioningflag" {
  count                            = var.isMultiregion == true ? 1 : 0 
  depends_on = [module.azurerm_traffic_manager_endpoint,resource.azurerm_virtual_network_peering.appgateway_to_hub,resource.azurerm_virtual_network_peering.hub_to_appgateway,resource.azurerm_subnet_route_table_association.sn_rt_association]
}

resource "azurerm_public_ip" "ukwest" {
  count               = var.isMultiregion == true ? 1 : 0
  name                = var.customer_type == "public" ? module.naming_ukwest.public_ip.name : module.naming_enterprise_ukwest.public_ip.name
  resource_group_name = azurerm_resource_group.app_gateway_ukwest[0].name
  location            = azurerm_resource_group.app_gateway_ukwest[0].location
  allocation_method   = var.allocation_method
  sku                 = var.public_ip_sku
  domain_name_label   = var.customer_type == "public" ? module.naming_ukwest.public_ip.name : module.naming_enterprise_ukwest.public_ip.name
  availability_zone   = var.public_ip_availability_zone
  depends_on = [null_resource.uksouthprovisioningflag]
  }

resource "azurerm_virtual_network_peering" "appgateway_to_hub_ukwest" {
  count                            = var.isMultiregion == true ? 1 : 0 
  name                      = var.virtual_network_peering_appgateway_to_firewall_name_ukwest
  resource_group_name       = azurerm_resource_group.app_gateway_ukwest[0].name
  virtual_network_name      = module.network-ukwest[0].vnet_name
  remote_virtual_network_id = data.azurerm_virtual_network.hubvnet.id
  allow_virtual_network_access = true
  allow_forwarded_traffic = true
  allow_gateway_transit   = false
  depends_on = [null_resource.uksouthprovisioningflag,module.network-ukwest , module.application_gateway_ukwest]

}
resource "azurerm_virtual_network_peering" "hub_to_appgateway_ukwest" {
  count                            = var.isMultiregion == true ? 1 : 0 
  name                      = var.virtual_network_peering_firewall_to_appgateway_ukwest
  resource_group_name       = var.hub_vnet_resource_group
  virtual_network_name      = data.azurerm_virtual_network.hubvnet.name
  remote_virtual_network_id = module.network-ukwest[0].vnet_id
  allow_virtual_network_access = true
  allow_forwarded_traffic = true
  allow_gateway_transit   = false
  provider                = azurerm.hub
  depends_on = [null_resource.uksouthprovisioningflag,module.network-ukwest , module.application_gateway_ukwest]
}

resource "azurerm_subnet_route_table_association" "sn_rt_association_ukwest" {
  count                            = var.isMultiregion == true ? 1 : 0 
  subnet_id      = module.network-ukwest[0].vnet_subnets[0]
  route_table_id = module.azure_network_route_table_platform_ukwest[0].route_table_id

  depends_on = [null_resource.uksouthprovisioningflag,module.network-ukwest, module.azure_network_route_table_platform_ukwest]
}