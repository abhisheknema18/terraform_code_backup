module "azure_network_route_table_platform_ukwest" {
  count                            = var.isMultiregion == true ? 1 : 0
  source = "../modules/routetable/"

  name                           = var.customer_type == "public" ? module.naming_ukwest.route_table.name : module.naming_enterprise_ukwest.route_table.name
  location                       = var.region_sec
  resource_group_name            = azurerm_resource_group.app_gateway_ukwest[0].name
  disable_bgp_route_propagation  = var.routetable_disable_bgp_route_propagation
  enable_force_tunneling         = var.routetable_enable_force_tunneling
  route_address_prefix           = var.route_address_prefix_ukwest
  next_hop_type                  = var.routetable_next_hop_type
  next_hop_in_ip_address         = var.routetable_next_hop_in_ip_address
  depends_on = [null_resource.uksouthprovisioningflag]
}
