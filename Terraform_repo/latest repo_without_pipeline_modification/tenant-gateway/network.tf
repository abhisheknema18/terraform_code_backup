module "network" {
  source                                                = "../modules/network/"
  vnet_name                                             = var.customer_type == "public" ? module.naming.virtual_network.name : module.naming_enterprise.virtual_network.name
  resource_group_name                                   = azurerm_resource_group.app_gateway_rg.name
  location                                              = var.region
  address_spaces                                        = var.network_address_spaces
  subnet_prefixes                                       = var.network_subnet_prefixes
  subnet_names                                          = var.network_subnet_names
  }

