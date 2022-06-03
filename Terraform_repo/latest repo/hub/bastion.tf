resource "azurerm_bastion_host" "this" {
  location            = azurerm_resource_group.shared.location
  name                = var.bastion_name
  resource_group_name = azurerm_resource_group.shared.name
  tags                = var.bastion_tags

  ip_configuration {
      name = var.bastion_public_ip_conf_name
      public_ip_address_id = azurerm_public_ip.bastion_ip.id
      subnet_id = module.network.vnet_subnets[1]
    }
}

resource "azurerm_public_ip" "bastion_ip" {
  name                = var.bastion_public_ip_name
  resource_group_name = azurerm_resource_group.shared.name
  location            = azurerm_resource_group.shared.location
  allocation_method   = var.bastion_public_ip_allocation_method
  sku                 = var.bastion_public_ip_sku
  domain_name_label   = var.bastion_public_ip_dns
}