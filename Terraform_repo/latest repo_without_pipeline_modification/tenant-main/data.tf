data "azurerm_container_registry" "platform" {
  provider            = azurerm.platform
  name                = var.acr_name
  resource_group_name = var.platform_shared_rg_name
}

data "azurerm_subnet" "platform" {
  provider                  = azurerm.platform
  name                      = var.platform_operationvm_subnet_name
  resource_group_name       = var.platform_shared_rg_name
  virtual_network_name      = var.platform_operationvm_vnet_name
}

data "azurerm_subnet" "platform_ado" {
  provider                  = azurerm.platform
  name                      = var.platform_adovm_subnet_name
  resource_group_name       = var.platform_shared_rg_name
  virtual_network_name      = var.platform_operationvm_vnet_name
}
data "azurerm_log_analytics_workspace" "platform" {
  provider            = azurerm.platform
  name                = var.log_analytics_workspace_name
  resource_group_name = var.platform_shared_rg_name
}

data "azurerm_resource_group" "platform_rg" {
  provider            = azurerm.platform
  name                =  var.platform_shared_rg_name
}
data "azurerm_client_config" "current" {
}

data "azurerm_virtual_network" "hubvnet" {
  provider            = azurerm.hub
  name                = var.hub_vnet_name
  resource_group_name = var.hub_vnet_resource_group
}

data "azurerm_virtual_network" "platformvnet" {
  provider            = azurerm.platform
  name                = var.platform_operationvm_vnet_name
  resource_group_name = var.platform_shared_rg_name
}

data "azurerm_mssql_server" "sqlserver" {
  name                         = var.tenant_primary_sqlserver_name
  resource_group_name          = var.tenant_sqlserver_resource_group
}

