module "network-ukwest" {
  count                                                 = var.isMultiregion == true ? 1 : 0
  source                                                = "../modules/network/"
  vnet_name                                             = var.customer_type == "public" ? module.naming_ukwest.virtual_network.name : module.naming_enterprise_ukwest.virtual_network.name
  resource_group_name                                   = azurerm_resource_group.app_gateway_ukwest[0].name
  location                                              = var.region_sec
  address_spaces                                        = var.network_address_spaces_ukwest
  subnet_prefixes                                       = var.network_subnet_prefixes_ukwest
  subnet_names                                          = var.network_subnet_names
   depends_on = [null_resource.uksouthprovisioningflag]
}

