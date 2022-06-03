module "network" {
  source                                                = "../modules/network/"
  vnet_name                                             = module.naming.virtual_network.name
  resource_group_name                                   = azurerm_resource_group.this.name
  location                                              = var.region
  address_spaces                                        = var.network_address_spaces
  subnet_prefixes                                       = var.network_subnet_prefixes
  subnet_names                                          = var.network_subnet_names
  subnet_enforce_private_link_endpoint_network_policies = var.network_subnet_enforce_private_link_endpoint_network_policies
  subnet_service_endpoints                              = var.subnet_service_endpoints
}

