data "azurerm_firewall_policy" "fw_policy" {
  name                = var.fwp_name
  resource_group_name = var.fwp_resource_group_name
  provider            = azurerm.hub
}

data "azurerm_resource_group" "shared" {
  name                = var.fwp_resource_group_name
  provider            = azurerm.hub
}

data "azurerm_client_config" "current" {
}
