data "azurerm_mssql_server" "primary" {
  name                = var.sql_server_name_primary
  resource_group_name = var.shared_resource_group_name
}

data "azurerm_mssql_server" "sec" {
  name                = var.sql_server_name_secondary
  resource_group_name = var.shared_resource_group_name
}

data "azurerm_mssql_elasticpool" "primary" {

  name = var.sql_ep_name
  resource_group_name = var.shared_resource_group_name
  server_name = data.azurerm_mssql_server.primary.name
}

data "azurerm_mssql_elasticpool" "sec" {

  name = var.sql_ep_name
  resource_group_name = var.shared_resource_group_name
  server_name = data.azurerm_mssql_server.sec.name
}

data "azurerm_log_analytics_workspace" "platform" {
  provider            = azurerm.platform
  name                = var.log_analytics_workspace_name
  resource_group_name = var.platform_shared_rg_name
}

data "azurerm_resource_group" "shared" {
  name                = var.shared_resource_group_name
}