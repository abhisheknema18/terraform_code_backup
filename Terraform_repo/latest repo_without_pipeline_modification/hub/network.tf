module "network" {
  source              = "../modules/network/"
  vnet_name           = module.naming_shared.virtual_network.name
  resource_group_name = azurerm_resource_group.shared.name
  location            = azurerm_resource_group.shared.location
  address_spaces      = var.network_address_spaces
  subnet_prefixes     = var.network_subnet_prefixes
  subnet_names        = var.network_subnet_names
}