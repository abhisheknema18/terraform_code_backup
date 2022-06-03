resource "azurerm_public_ip" "this" {
  name                = var.customer_type == "public" ? module.naming.public_ip.name : module.naming_enterprise.public_ip.name
  resource_group_name = azurerm_resource_group.app_gateway_rg.name
  location            = azurerm_resource_group.app_gateway_rg.location
  allocation_method   = var.allocation_method
  sku                 = var.public_ip_sku
  domain_name_label   =  var.customer_type == "public" ? module.naming.public_ip.name : module.naming_enterprise.public_ip.name
  }

resource "azurerm_virtual_network_peering" "appgateway_to_hub" {
  name                      = var.virtual_network_peering_appgateway_to_firewall_name
  resource_group_name       = azurerm_resource_group.app_gateway_rg.name
  virtual_network_name      = module.network.vnet_name
  remote_virtual_network_id = data.azurerm_virtual_network.hubvnet.id
  allow_virtual_network_access = true
  allow_forwarded_traffic = true
  allow_gateway_transit   = false
  depends_on = [module.network , module.application_gateway]

}
resource "azurerm_virtual_network_peering" "hub_to_appgateway" {
  name                      = var.virtual_network_peering_firewall_to_appgateway
  resource_group_name       = var.hub_vnet_resource_group
  virtual_network_name      = data.azurerm_virtual_network.hubvnet.name
  remote_virtual_network_id = module.network.vnet_id
  allow_virtual_network_access = true
  allow_forwarded_traffic = true
  allow_gateway_transit   = false
  provider                = azurerm.hub
  depends_on = [module.network , module.application_gateway]
}

resource "azurerm_subnet_route_table_association" "sn_rt_association" {
  subnet_id      = module.network.vnet_subnets[0]
  route_table_id = module.azure_network_route_table_platform.route_table_id

  depends_on = [module.network, module.azure_network_route_table_platform]
}