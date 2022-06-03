resource "azurerm_resource_group" "global" {
  name     = module.naming_shared.resource_group.name
  location = var.region
}
