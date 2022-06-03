resource "azurerm_container_registry" "acr" {
  name                          = module.naming.container_registry.name
  resource_group_name           = azurerm_resource_group.this.name
  location                      = azurerm_resource_group.this.location
  sku                           = "Premium"
  admin_enabled                 = true
  public_network_access_enabled = var.public_network_access_enabled
}

resource "azurerm_monitor_diagnostic_setting" "this" {
  name = "${module.naming.container_registry.name}-acr-diag"
  log_analytics_workspace_id = azurerm_log_analytics_workspace.this[0].id
  target_resource_id = azurerm_container_registry.acr.id
  log  {category = "ContainerRegistryRepositoryEvents"}
  log {category = "ContainerRegistryLoginEvents"}
  metric { category = "AllMetrics"}
  depends_on = [azurerm_log_analytics_workspace.this]
  }