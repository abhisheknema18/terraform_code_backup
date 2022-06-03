resource "azurerm_resource_group" "this" {
  name     = module.naming.resource_group.name
  location = var.region
}

resource "azurerm_resource_group" "global" {
  name     = module.naming_global.resource_group.name
  location = var.region
}
