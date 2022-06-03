module "azure_network_route_table_aks-ukwest" {
  count                            = var.isMultiregion == true ? 1 : 0 
  source                        = "../modules/routetable/"

  name                          = format("%s-aks", module.naming_ukwest.route_table.name)
  location                      = var.region_sec
  resource_group_name           = azurerm_resource_group.ukwest[0].name

  disable_bgp_route_propagation = var.routetable_disable_bgp_route_propagation
  enable_force_tunneling        = var.routetable_enable_force_tunneling

  next_hop_type                 = var.routetable_next_hop_type
  next_hop_in_ip_address        = var.routetable_next_hop_in_ip_address
  depends_on = [null_resource.uksouthprovisioningflag]
}