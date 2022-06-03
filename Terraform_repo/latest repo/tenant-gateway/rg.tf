resource "azurerm_resource_group" "app_gateway_rg" {
  name     = module.naming_application_gateway.resource_group.name
  location = var.region
}


