resource "azurerm_subnet_route_table_association" "sn_rt_association" {
  count          = length(var.network_subnet_names)
  subnet_id      = module.network.vnet_subnets[count.index]
  route_table_id = module.azure_network_route_table_platform.route_table_id

  depends_on = [module.network, module.azure_network_route_table_platform]
}

resource "azurerm_virtual_network_peering" "peering1" {
  name                      = var.virtual_network_peering_platform_to_firewall_name
  resource_group_name       = azurerm_resource_group.this.name
  virtual_network_name      = module.network.vnet_name
  remote_virtual_network_id = data.azurerm_virtual_network.hubvnet.id
  allow_virtual_network_access = true
  allow_forwarded_traffic = true
  allow_gateway_transit   = false
  depends_on = [module.network]

}
resource "azurerm_virtual_network_peering" "peering2" {
  name                      = var.virtual_network_peering_firewall_to_platform
  resource_group_name       = var.hub_vnet_resource_group
  virtual_network_name      = data.azurerm_virtual_network.hubvnet.name
  remote_virtual_network_id = module.network.vnet_id
  allow_virtual_network_access = true
  allow_forwarded_traffic = true
  allow_gateway_transit   = false
  provider                = azurerm.hub
  depends_on = [module.network]
}


resource "azurerm_subnet_network_security_group_association" "ADO_subnet_nsg_association" {
  subnet_id                 = module.network.vnet_subnets[2]
  network_security_group_id = module.ado_nsg.network_security_group_id
  depends_on = [module.ado_nsg]
}

resource "azurerm_subnet_network_security_group_association" "ops_subnet_nsg_association" {
  subnet_id                 = module.network.vnet_subnets[3]
  network_security_group_id = module.ops_nsg.network_security_group_id
  depends_on = [module.ops_nsg]
}

resource "azurerm_monitor_diagnostic_setting" "firewall_dig_setting" {
  name = "firewall-diag"
  log_analytics_workspace_id = azurerm_log_analytics_workspace.this[0].id
  target_resource_id = data.azurerm_firewall.firewall-diag-setting.id
  log  {category = "AzureFirewallApplicationRule"}
  log {category = "AzureFirewallNetworkRule"}
  log {category = "AzureFirewallDnsProxy"}
  metric { category = "AllMetrics"}
  provider = azurerm.hub
  depends_on = [azurerm_log_analytics_workspace.this]
}

resource "azurerm_private_dns_zone" "acrprivatednszone" {
  name                = var.azurerm_private_dns_zone_acr_name
  resource_group_name = azurerm_resource_group.this.name
}

resource "azurerm_private_endpoint" "privateendpointacrtoadoagent" {
  name                = "${module.naming.private_endpoint.name}-acr-ado-agent"
  location            = var.region
  resource_group_name = azurerm_resource_group.this.name
  subnet_id           = module.network.vnet_subnets[2]

 private_dns_zone_group {
    name                 = module.naming.private_dns_zone_group.name
    private_dns_zone_ids = [azurerm_private_dns_zone.acrprivatednszone.id]
 }

  private_service_connection {
    name                           = module.naming.private_service_connection.name
    private_connection_resource_id = azurerm_container_registry.acr.id
    is_manual_connection           = false
    subresource_names              = ["registry"]
  }
     depends_on = [module.ado-build-agent-vm, module.ado-deploy-agent-vm, module.network]
}

 resource "azurerm_private_dns_zone_virtual_network_link" "vnetlinkadoagenttoacr" {
  name                  = "link-plat-to-acr"
  resource_group_name   = azurerm_resource_group.this.name
  private_dns_zone_name = azurerm_private_dns_zone.acrprivatednszone.name
  virtual_network_id    = module.network.vnet_id
  depends_on = [azurerm_private_endpoint.privateendpointacrtoadoagent]
}