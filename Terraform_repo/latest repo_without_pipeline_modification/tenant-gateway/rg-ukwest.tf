resource "azurerm_resource_group" "app_gateway_ukwest" {
  count                            = var.isMultiregion == true ? 1 : 0
  name     = module.naming_application_gateway_ukwest.resource_group.name
  location = var.region_sec
  depends_on = [null_resource.uksouthprovisioningflag]
}


