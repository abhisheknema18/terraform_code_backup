resource "azurerm_private_dns_zone" "sqlprivatednszone" {
  provider            = azurerm.platform
  name                = var.azurerm_private_dns_zone_sql_name
  resource_group_name = var.platform_shared_rg_name
}

resource "azurerm_private_endpoint" "privateendpointsqltoopsvm" {
  provider            = azurerm.platform
  name                = "${module.naming.private_endpoint.name}-sql-opsvm"
  location            = var.region
  resource_group_name = var.platform_shared_rg_name
  subnet_id           = data.azurerm_subnet.platform.id

  private_dns_zone_group {
    name                 = module.naming.private_dns_zone_group.name
    private_dns_zone_ids = [azurerm_private_dns_zone.sqlprivatednszone.id]
  }

  private_service_connection {
    name                           = module.naming.private_service_connection.name
    private_connection_resource_id = module.sqlserver.id
    is_manual_connection           = false
    subresource_names              = ["sqlServer"]
  }
  depends_on = [module.sqlserver, module.sqlserver_sec]
}



resource "azurerm_private_endpoint" "privateendpointsqltoadovm" {
  provider            = azurerm.platform
  name                = "${module.naming.private_endpoint.name}-sql-adovm"
  location            = var.region
  resource_group_name = var.platform_shared_rg_name
  subnet_id           = data.azurerm_subnet.platform_ado_subnet.id

  private_dns_zone_group {
    name                 = "default"
    private_dns_zone_ids = [azurerm_private_dns_zone.sqlprivatednszone.id]
  }

  private_service_connection {
    name                           = "private_service_connection_name"
    private_connection_resource_id = module.sqlserver.id
    is_manual_connection           = false
    subresource_names              = ["sqlServer"]
  }
  depends_on = [module.sqlserver, module.sqlserver_sec, azurerm_private_endpoint.privateendpointsqltoopsvm]
}


#resource "azurerm_private_dns_a_record" "ado_private_endpoint_interface_ip" {
#  provider            = azurerm.platform
#  name                = "ado"
#  zone_name           = azurerm_private_dns_zone.sqlprivatednszone.name
#  resource_group_name = var.platform_shared_rg_name
#  ttl                 = 300
#  records             = [azurerm_private_endpoint.privateendpointsqltoadovm.private_service_connection[0].private_ip_address]
#}

resource "azurerm_private_dns_a_record" "ops_private_endpoint_interface_ip" {
  provider            = azurerm.platform
  name                = "ops"
  zone_name           = azurerm_private_dns_zone.sqlprivatednszone.name
  resource_group_name = var.platform_shared_rg_name
  ttl                 = 300
  records             = [azurerm_private_endpoint.privateendpointsqltoopsvm.private_service_connection[0].private_ip_address]
  depends_on = [azurerm_private_endpoint.privateendpointsqltoopsvm,azurerm_private_endpoint.privateendpointsqltoadovm]
}


resource "azurerm_private_dns_zone_virtual_network_link" "vnetlinkplattosql" {
  provider              = azurerm.platform
  name                  = "link-plat-to-sql"
  resource_group_name   = var.platform_shared_rg_name
  private_dns_zone_name = azurerm_private_dns_zone.sqlprivatednszone.name
  virtual_network_id    = data.azurerm_virtual_network.platformvnet.id
  depends_on = [azurerm_private_endpoint.privateendpointsqltoopsvm,azurerm_private_endpoint.privateendpointsqltoadovm]
}


