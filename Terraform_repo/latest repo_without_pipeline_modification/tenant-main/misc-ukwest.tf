

resource "azurerm_subnet_route_table_association" "sn_rt_association_aks_firewall-ukwest" {
  count                            = var.isMultiregion == true ? 1 : 0 
  subnet_id      = module.network-ukwest[0].vnet_subnets[0]
  route_table_id = module.azure_network_route_table_aks-ukwest[0].route_table_id

  depends_on = [null_resource.uksouthprovisioningflag, module.network-ukwest, module.azure_network_route_table_aks-ukwest]
}

resource "null_resource" "uksouthprovisioningflag" {
  count                            = var.isMultiregion == true ? 1 : 0 
  depends_on = [module.aks,module.windowsservers,resource.azurerm_subnet_route_table_association.sn_rt_association_aks_firewall]
}

resource "azurerm_virtual_network_peering" "hub-to-ukwest" {
  count                     = var.isMultiregion == true ? 1 : 0 
  name                      = format("hub-to-%s-%s", var.env, var.region_sec)
  resource_group_name       = azurerm_resource_group.ukwest[0].name
  virtual_network_name      = module.network-ukwest[0].vnet_name
  remote_virtual_network_id = data.azurerm_virtual_network.hubvnet.id

  allow_virtual_network_access = true
  allow_forwarded_traffic = true
  allow_gateway_transit   = false
  depends_on = [null_resource.uksouthprovisioningflag, module.network-ukwest]
}

resource "azurerm_virtual_network_peering" "ukwest-to-hub" {
  count                     = var.isMultiregion == true ? 1 : 0
  name                      = format("hub-to-%s-%s", var.env, var.region_sec)
  resource_group_name       = var.hub_vnet_resource_group
  virtual_network_name      = data.azurerm_virtual_network.hubvnet.name
  remote_virtual_network_id = module.network-ukwest[0].vnet_id

  allow_virtual_network_access = true
  allow_forwarded_traffic = true
  allow_gateway_transit   = false
  provider                = azurerm.hub
  depends_on = [null_resource.uksouthprovisioningflag, module.network-ukwest]
}


resource "azurerm_private_dns_zone" "sqlprivatednszoneukwest" {
  count                     = var.isMultiregion == true ? 1 : 0
  name                = var.azurerm_private_dns_zone_sql_name
  resource_group_name = azurerm_resource_group.ukwest[0].name
  depends_on = [null_resource.uksouthprovisioningflag]
}

resource "azurerm_private_dns_zone" "acrprivatednszoneukwest" {
  count                     = var.isMultiregion == true ? 1 : 0
  name                = var.azurerm_private_dns_zone_acr_name
  resource_group_name = azurerm_resource_group.ukwest[0].name
  depends_on = [null_resource.uksouthprovisioningflag]
}

resource "azurerm_private_endpoint" "privateendpointsqltoaksukwest" {
  count               = var.isMultiregion == true ? 1 : 0
  name                = "${module.naming_ukwest.private_endpoint.name}-sql-aks"
  location            = var.region_sec
  resource_group_name = azurerm_resource_group.ukwest[0].name
  subnet_id           = module.network-ukwest[0].vnet_subnets[0]
  

 private_dns_zone_group {
    name                 = module.naming_ukwest.private_dns_zone_group.name
    private_dns_zone_ids = [azurerm_private_dns_zone.sqlprivatednszoneukwest[0].id]
 }

  private_service_connection {
    name                           = module.naming_ukwest.private_service_connection.name
    private_connection_resource_id = data.azurerm_mssql_server.sqlserver.id
    is_manual_connection           = false
    subresource_names              = ["sqlServer"]
  }
   depends_on = [null_resource.uksouthprovisioningflag, module.aks-ukwest]
}

 resource "azurerm_private_dns_zone_virtual_network_link" "vnetlinksqltoaksukwest" {
   count               = var.isMultiregion == true ? 1 : 0
  name                  = "link-aks-ukwest-to-sql"
  resource_group_name   = azurerm_resource_group.ukwest[0].name
  private_dns_zone_name = azurerm_private_dns_zone.sqlprivatednszoneukwest[0].name
  virtual_network_id    = module.network-ukwest[0].vnet_id
  depends_on = [null_resource.uksouthprovisioningflag,azurerm_private_endpoint.privateendpointsqltoaksukwest]
}

 resource "azurerm_private_dns_zone_virtual_network_link" "vnetlinkacrtoaksukwest" {
    count               = var.isMultiregion == true ? 1 : 0
  name                  = "link-aks-ukwest-to-acr"
  resource_group_name   = azurerm_resource_group.ukwest[0].name
  private_dns_zone_name = azurerm_private_dns_zone.acrprivatednszoneukwest[0].name
  virtual_network_id    = module.network-ukwest[0].vnet_id
  depends_on = [null_resource.uksouthprovisioningflag,azurerm_private_endpoint.privateendpointacrtoaksukwest]
}

resource "azurerm_private_endpoint" "privateendpointacrtoaksukwest" {
  count               = var.isMultiregion == true ? 1 : 0
  name                = "${module.naming_ukwest.private_endpoint.name}-acr"
  location            = var.region_sec
  resource_group_name = azurerm_resource_group.ukwest[0].name
  subnet_id           = module.network-ukwest[0].vnet_subnets[0]

 private_dns_zone_group {
    name                 = module.naming_ukwest.private_dns_zone_group.name
    private_dns_zone_ids = [azurerm_private_dns_zone.acrprivatednszoneukwest[0].id]
 }

  private_service_connection {
    name                           = module.naming_ukwest.private_service_connection.name
    private_connection_resource_id = data.azurerm_container_registry.platform.id
    is_manual_connection           = false
    subresource_names              = ["registry"]
  }
     depends_on = [null_resource.uksouthprovisioningflag, module.aks-ukwest]
}

 resource "azurerm_private_dns_zone_virtual_network_link" "vnetlinkplattoaksukwest" {
   count               = var.isMultiregion == true ? 1 : 0
  name                  = "link-aks-ukwest-to-plat"
  resource_group_name   = module.aks-ukwest[0].node_resource_group
  private_dns_zone_name = module.aks-ukwest[0].private_dns_zone_name
  virtual_network_id    = data.azurerm_virtual_network.platformvnet.id
  depends_on = [null_resource.uksouthprovisioningflag,module.aks-ukwest]
}

resource "azurerm_role_assignment" "vnet_permission_for_aks_ukwest" {
  count               = var.isMultiregion == true ? 1 : 0
  scope                = module.network-ukwest[0].vnet_id
  role_definition_name = "Contributor"
  principal_id         = module.aks-ukwest[0].system_assigned_identity[0].principal_id
  depends_on = [null_resource.uksouthprovisioningflag]

}

resource "azurerm_key_vault_access_policy" "aksukwest_to_kvpolicy" {
  count               = var.isMultiregion == true ? 1 : 0
  key_vault_id        = module.azurerm_key_vault_mutilregion[0].id
  tenant_id           = data.azurerm_client_config.current.tenant_id
  object_id           = module.aks-ukwest[0].kubelet_identity[0].object_id

  key_permissions         = var.kv_key_permissions_read
  secret_permissions      = var.kv_secret_permissions_read
  certificate_permissions = var.kv_certificate_permissions_read
  storage_permissions     = var.kv_storage_permissions_no_access

  depends_on = [null_resource.uksouthprovisioningflag, module.aks-ukwest]
}