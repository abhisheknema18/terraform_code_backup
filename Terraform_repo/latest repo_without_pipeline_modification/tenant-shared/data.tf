data "azurerm_log_analytics_workspace" "platform" {
  provider            = azurerm.platform
  name                = var.log_analytics_workspace_name
  resource_group_name = var.platform_shared_rg_name
}

data "azurerm_subnet" "platform" {
  provider                  = azurerm.platform
  name                      = var.platform_operationvm_subnet_name
  resource_group_name       = var.platform_shared_rg_name
  virtual_network_name      = var.platform_operationvm_vnet_name
}

data "azurerm_subnet" "platform_ado_subnet" {
  provider                  = azurerm.platform
  name                      = var.platform_adovm_subnet_name
  resource_group_name       = var.platform_shared_rg_name
  virtual_network_name      = var.platform_operationvm_vnet_name
}


data "azurerm_virtual_network" "platformvnet" {
  provider            = azurerm.platform
  name                = var.platform_operationvm_vnet_name
  resource_group_name = var.platform_shared_rg_name
}