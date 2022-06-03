module "ipgroups" {

  source = "../modules/ipgroup"
  location = azurerm_resource_group.shared.location
  resource_group_name = azurerm_resource_group.shared.name
  names = var.ipgroup_names
  cidrs = var.ipgroup_cidrs
}