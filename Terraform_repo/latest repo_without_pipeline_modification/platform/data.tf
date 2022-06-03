data "azurerm_client_config" "current" {
}

data "azurerm_virtual_network" "hubvnet" {
  provider            = azurerm.hub
  name                = var.hub_vnet_name
  resource_group_name = var.hub_vnet_resource_group
}

data "azurerm_firewall" "firewall-diag-setting" {
  name                = "fw-hub-${var.env}-shared"
  resource_group_name = var.hub_vnet_resource_group
  provider            = azurerm.hub
}