resource "azurerm_route_table" "this" {
  name                = var.name
  location            = var.location
  resource_group_name = var.resource_group_name

  disable_bgp_route_propagation = var.disable_bgp_route_propagation
}

resource "azurerm_route" "this" {
  name                = "InternetForceTunneling"
  resource_group_name = var.resource_group_name
  route_table_name    = azurerm_route_table.this.name
  address_prefix      = var.route_address_prefix
  next_hop_type       = var.next_hop_type
  next_hop_in_ip_address = var.next_hop_in_ip_address

  count = var.enable_force_tunneling ? 1 : 0
}