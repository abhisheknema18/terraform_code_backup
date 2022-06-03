module "network-ukwest" {
  count                = var.isMultiregion == true ? 1 : 0
  source              = "../modules/network/"
  vnet_name           = module.naming_ukwest.virtual_network.name
  resource_group_name = azurerm_resource_group.ukwest[0].name
  location            = var.region_sec
  address_spaces      = var.network_address_spaces_ukwest
  subnet_prefixes     = var.network_subnet_prefixes_ukwest
  subnet_names        = var.network_subnet_names
  subnet_enforce_private_link_endpoint_network_policies = var.network_subnet_enforce_private_link_endpoint_network_policies
  subnet_service_endpoints = var.subnet_service_endpoints
  #depends_on = [null_resource.uksouthprovisioningflag]
}

