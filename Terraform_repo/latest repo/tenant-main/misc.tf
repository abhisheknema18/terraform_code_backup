resource "null_resource" "kv_upload_keys" {

  provisioner "local-exec" {
    interpreter = ["sh", "-c"]
    #interpreter = ["/bin/bash", "-c"]
    command = templatefile("${path.module}/azcmd.sh.tmpl",
    {
      KEY_VAULT_NAME = module.naming.key_vault.name
      PRIVATE_KEY_FILENAME = var.ssh_integration_service_private_key_filename
      PUBLIC_KEY_FILENAME = var.ssh_integration_service_public_key_filename
      PKCS8_PRIVATE_KEY = var.ssh_integration_service_pkcs8_private_key_filename
      PRIVATE_KEY_SECRET_NAME = var.kv_secret_private_key
      PUBLIC_KEY_SECRET_NAME = var.kv_secret_public_key
    })
  }
  depends_on = [module.integration_ssh_key,module.azurerm_key_vault,  module.azurerm_key_vault_mutilregion]
}


resource "azurerm_subnet_route_table_association" "sn_rt_association_aks_firewall" {
  subnet_id      = module.network.vnet_subnets[0]
  route_table_id = module.azure_network_route_table_aks.route_table_id

  depends_on = [module.network, module.azure_network_route_table_aks]
}

resource "azurerm_subnet_route_table_association" "sn_rt_association_vm_sb_firewall" {
  subnet_id      = module.network_global.vnet_subnets[0]
  route_table_id = module.azure_network_route_table_vm_sb.route_table_id

    depends_on = [module.network, module.azure_network_route_table_vm_sb, module.windowsservers]
}

resource "azurerm_virtual_network_peering" "hub-to-uksouth" {
  name                      = format("hub-to-%s-%s", var.env, var.region)
  resource_group_name       = azurerm_resource_group.this.name
  virtual_network_name      = module.network.vnet_name
  remote_virtual_network_id = data.azurerm_virtual_network.hubvnet.id

  allow_virtual_network_access = true
  allow_forwarded_traffic = true
  allow_gateway_transit   = false
  depends_on = [module.network]
}

resource "azurerm_virtual_network_peering" "uksouth-to-hub" {
  name                      = format("hub-to-%s-%s", var.env, var.region)
  resource_group_name       = var.hub_vnet_resource_group
  virtual_network_name      = data.azurerm_virtual_network.hubvnet.name
  remote_virtual_network_id = module.network.vnet_id

  allow_virtual_network_access = true
  allow_forwarded_traffic = true
  allow_gateway_transit   = false
  provider                = azurerm.hub
  depends_on = [module.network]
}

resource "azurerm_virtual_network_peering" "hub-to-glb" {
  name                      = format("hub-to-%s-%s", var.env, var.app_global)
  resource_group_name       = azurerm_resource_group.global.name
  virtual_network_name      = module.network_global.vnet_name
  remote_virtual_network_id = data.azurerm_virtual_network.hubvnet.id

  allow_virtual_network_access = true
  allow_forwarded_traffic = true
  allow_gateway_transit   = false
  depends_on = [module.network]
}

resource "azurerm_virtual_network_peering" "glb-to-hub" {
  name                      = format("hub-to-%s-%s", var.env, var.app_global)
  resource_group_name       = var.hub_vnet_resource_group
  virtual_network_name      = data.azurerm_virtual_network.hubvnet.name
  remote_virtual_network_id = module.network_global.vnet_id

  allow_virtual_network_access = true
  allow_forwarded_traffic = true
  allow_gateway_transit   = false
  provider                = azurerm.hub
  depends_on = [module.network]
}


resource "azurerm_private_dns_zone" "sqlprivatednszoneuksouth" {
  name                = var.azurerm_private_dns_zone_sql_name
  resource_group_name = azurerm_resource_group.this.name
}

resource "azurerm_private_dns_zone" "sqlprivatednszonesb" {
  name                = var.azurerm_private_dns_zone_sql_name
  resource_group_name = azurerm_resource_group.global.name
}

resource "azurerm_private_dns_zone" "acrprivatednszoneuksouth" {
  name                = var.azurerm_private_dns_zone_acr_name
  resource_group_name = azurerm_resource_group.this.name
}


resource "azurerm_private_endpoint" "privateendpointsqltoaksuksouth" {
  name                = "${module.naming.private_endpoint.name}-sql-aks"
  location            = var.region
  resource_group_name = azurerm_resource_group.this.name
  subnet_id           = module.network.vnet_subnets[0]
  
 private_dns_zone_group {
    name                 = module.naming.private_dns_zone_group.name
    private_dns_zone_ids = [azurerm_private_dns_zone.sqlprivatednszoneuksouth.id]
 }

  private_service_connection {
    name                           = module.naming.private_service_connection.name
    private_connection_resource_id = data.azurerm_mssql_server.sqlserver.id
    is_manual_connection           = false
    subresource_names              = ["sqlServer"]
  }
   depends_on = [module.aks]
}

resource "azurerm_private_endpoint" "privateendpointacrtoaksuksouth" {
  name                = "${module.naming.private_endpoint.name}-acr-aks"
  location            = var.region
  resource_group_name = azurerm_resource_group.this.name
  subnet_id           = module.network.vnet_subnets[0]

 private_dns_zone_group {
    name                 = module.naming.private_dns_zone_group.name
    private_dns_zone_ids = [azurerm_private_dns_zone.acrprivatednszoneuksouth.id]
 }

  private_service_connection {
    name                           = module.naming.private_service_connection.name
    private_connection_resource_id = data.azurerm_container_registry.platform.id
    is_manual_connection           = false
    subresource_names              = ["registry"]
  }
     depends_on = [module.aks]
}

resource "azurerm_private_endpoint" "privateendpointsqltosb" {
  name                = "${module.naming.private_endpoint.name}-sql-sb"
  location            = var.region
  resource_group_name = azurerm_resource_group.global.name
  subnet_id           = module.network_global.vnet_subnets[0]

 private_dns_zone_group {
    name                 =  module.naming.private_dns_zone_group.name
    private_dns_zone_ids = [azurerm_private_dns_zone.sqlprivatednszonesb.id]
 }

  private_service_connection {
    name                           = module.naming.private_service_connection.name
    private_connection_resource_id = data.azurerm_mssql_server.sqlserver.id
    is_manual_connection           = false
    subresource_names              = ["sqlServer"]
  }
     depends_on = [module.windowsservers]
}

 resource "azurerm_private_dns_zone_virtual_network_link" "vnetlinksqltosb" {
  name                  = "link-sb-to-sql"
  resource_group_name   = azurerm_resource_group.global.name
  private_dns_zone_name = azurerm_private_dns_zone.sqlprivatednszonesb.name
  virtual_network_id    = module.network_global.vnet_id
  depends_on = [azurerm_private_endpoint.privateendpointsqltosb]
}

 resource "azurerm_private_dns_zone_virtual_network_link" "vnetlinksqltoaksuksouth" {
  name                  = "link-aks-uksouth-to-sql"
  resource_group_name   = azurerm_resource_group.this.name
  private_dns_zone_name = azurerm_private_dns_zone.sqlprivatednszoneuksouth.name
  virtual_network_id    = module.network.vnet_id
  depends_on = [azurerm_private_endpoint.privateendpointsqltoaksuksouth]
}

 resource "azurerm_private_dns_zone_virtual_network_link" "vnetlinkacrtoaksuksouth" {
  name                  = "link-aks-uksouth-to-acr"
  resource_group_name   = azurerm_resource_group.this.name
  private_dns_zone_name = azurerm_private_dns_zone.acrprivatednszoneuksouth.name
  virtual_network_id    = module.network.vnet_id
  depends_on = [azurerm_private_endpoint.privateendpointacrtoaksuksouth]
}

 resource "azurerm_private_dns_zone_virtual_network_link" "vnetlinkplattoaksuksouth" {
  name                  = "link-aks-uksouth-to-plat"
  resource_group_name   = module.aks.node_resource_group
  private_dns_zone_name = module.aks.private_dns_zone_name
  virtual_network_id    = data.azurerm_virtual_network.platformvnet.id
  depends_on = [module.aks]
}



resource "azurerm_role_assignment" "vnet_permission_for_aks_uksouth" {
  scope                = module.network.vnet_id
  role_definition_name = "Contributor"
  principal_id         = module.aks.system_assigned_identity[0].principal_id

}