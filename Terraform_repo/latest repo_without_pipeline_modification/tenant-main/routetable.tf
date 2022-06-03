module "azure_network_route_table_aks" {
  source                        = "../modules/routetable/"

  name                          = format("%s-aks", module.naming.route_table.name)
  location                      = var.region
  resource_group_name           = azurerm_resource_group.this.name

  disable_bgp_route_propagation = var.routetable_disable_bgp_route_propagation
  enable_force_tunneling        = var.routetable_enable_force_tunneling

  next_hop_type                 = var.routetable_next_hop_type
  next_hop_in_ip_address        = var.routetable_next_hop_in_ip_address
}

module "azure_network_route_table_vm_sb" {
  source                        = "../modules/routetable/"

  name                          = format("%s", module.naming_global.route_table.name)
  location                      = azurerm_resource_group.global.location
  resource_group_name           = azurerm_resource_group.global.name

  disable_bgp_route_propagation = var.routetable_disable_bgp_route_propagation
  enable_force_tunneling        = var.routetable_enable_force_tunneling

  next_hop_type                 = var.routetable_next_hop_type
  next_hop_in_ip_address        = var.routetable_next_hop_in_ip_address
}