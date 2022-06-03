resource "azurerm_resource_group" "shared" {
  name     = module.naming_shared.resource_group.name
  location = var.region
}
