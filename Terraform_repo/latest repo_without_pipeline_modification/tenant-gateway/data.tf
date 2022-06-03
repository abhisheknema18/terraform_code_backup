
data "azurerm_client_config" "current" {
}

data "azurerm_virtual_network" "hubvnet" {
  provider            = azurerm.hub
  name                = var.hub_vnet_name
  resource_group_name = var.hub_vnet_resource_group
}

data "azurerm_log_analytics_workspace" "platform" {
  provider            = azurerm.platform
  name                = var.log_analytics_workspace_name
  resource_group_name = var.platform_shared_rg_name
}

data "azurerm_resource_group" "shared" {
  name                = var.fwp_resource_group_name
  provider            = azurerm.hub
}