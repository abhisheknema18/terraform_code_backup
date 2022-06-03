resource "azurerm_log_analytics_workspace" "this" {
  count               = var.log_analytics_workspace_count
  name                = module.naming.log_analytics_workspace.name
  location            = azurerm_resource_group.this.location
  resource_group_name = azurerm_resource_group.this.name
  sku                 = var.log_analytics_workspace_sku
  retention_in_days   = var.log_retention_in_days
}

resource "azurerm_log_analytics_solution" "this" {
  solution_name         = var.log_analytics_solution_name
  location              = azurerm_resource_group.this.location
  resource_group_name   = azurerm_resource_group.this.name
  workspace_resource_id = azurerm_log_analytics_workspace.this[0].id
  workspace_name        = azurerm_log_analytics_workspace.this[0].name

  plan {
    publisher = var.log_analytics_workspace_plan_publisher
    product   = var.log_analytics_workspace_plan_product
  }
}