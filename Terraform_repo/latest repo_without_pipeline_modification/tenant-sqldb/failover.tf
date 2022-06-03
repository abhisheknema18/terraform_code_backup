resource "azurerm_sql_failover_group" "this" {
  name                = module.naming.sql_failover_group.name
  resource_group_name = var.shared_resource_group_name
  server_name         = data.azurerm_mssql_server.primary.name

  databases           = module.sqldatabase.id

  partner_servers {
    id = data.azurerm_mssql_server.sec.id
  }

  read_write_endpoint_failover_policy {
    mode          = "Automatic"
    grace_minutes = 60
  }

  depends_on = [azurerm_mssql_database.secondary]
}