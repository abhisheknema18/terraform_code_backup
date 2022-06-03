module "azure_network_route_table_platform" {
  source = "../modules/routetable/"

  name                           = var.customer_type == "public" ? module.naming.route_table.name : module.naming_enterprise.route_table.name
  location                       = var.region
  resource_group_name            = azurerm_resource_group.app_gateway_rg.name
  disable_bgp_route_propagation  = var.routetable_disable_bgp_route_propagation
  enable_force_tunneling         = var.routetable_enable_force_tunneling
  route_address_prefix           = var.route_address_prefix
  next_hop_type                  = var.routetable_next_hop_type
  next_hop_in_ip_address         = var.routetable_next_hop_in_ip_address
}
