resource "azurerm_ip_group" "this" {
  count               = length(var.names)
  
  cidrs               = var.cidrs[count.index]
  name                = var.names[count.index]
  location            = var.location
  resource_group_name = var.resource_group_name
  tags                = var.tags
}