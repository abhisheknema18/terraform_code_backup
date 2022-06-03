data "azurerm_subnet" "subnet" {
  count                = length(var.sql_virtual_network_rule)
  name                 = lookup(var.sql_virtual_network_rule[count.index], "subnet_name")
  resource_group_name  = lookup(var.sql_virtual_network_rule[count.index], "virtual_network_resource_group")
  virtual_network_name = lookup(var.sql_virtual_network_rule[count.index], "virtual_network")
}

data "azurerm_storage_account" "storageaccount" {
  name                     = var.mssql_server_extended_auditing_policy_storage_account_name
  resource_group_name      = var.mssql_server_extended_auditing_policy_storage_account_resource_group_name
}

resource "azurerm_mssql_server" "sqlserver" {
  name                         = var.sql_server_name
  resource_group_name          = var.resource_group_name
  location                     = var.location
  version                      = var.sql_server_version
  administrator_login          = var.sql_server_administrator_login
  administrator_login_password = var.sql_server_administrator_login_password
  public_network_access_enabled= var.sql_public_network_access_enabled
  tags                         = var.tags

  azuread_administrator {
    login_username = lookup(var.sql_active_directory_administrator[0], "user_name")
    object_id      = lookup(var.sql_active_directory_administrator[0], "object_id")
  }

  identity {
    type = "SystemAssigned"
  }

}

resource "azurerm_sql_firewall_rule" "sqlfirewallrule" {
  count               = var.sql_public_network_access_enabled ? length(var.sql_firewall_rule) : 0
  name                = lookup(var.sql_firewall_rule[count.index], "name")
  resource_group_name = var.resource_group_name
  server_name         = azurerm_mssql_server.sqlserver.name
  start_ip_address    = lookup(var.sql_firewall_rule[count.index], "start_ip_address")
  end_ip_address      = lookup(var.sql_firewall_rule[count.index], "end_ip_address")
}

resource "azurerm_sql_virtual_network_rule" "sqlvnetrule" {
  count               = length(var.sql_virtual_network_rule)
  name                = lookup(var.sql_virtual_network_rule[count.index], "name")
  resource_group_name = var.resource_group_name
  server_name         = azurerm_mssql_server.sqlserver.name
  subnet_id           = data.azurerm_subnet.subnet[count.index].id
}

resource "azurerm_mssql_server_extended_auditing_policy" "mssqlserverextendedauditingpolicy" {
  server_id                               = azurerm_mssql_server.sqlserver.id
  storage_endpoint                        = data.azurerm_storage_account.storageaccount.primary_blob_endpoint
  storage_account_access_key              = data.azurerm_storage_account.storageaccount.primary_access_key
  storage_account_access_key_is_secondary = var.mssql_server_extended_auditing_policy_storage_account_access_key_is_secondary
  retention_in_days                       = var.mssql_server_extended_auditing_policy_retention_in_days
}

resource "null_resource" "sqlactivedirectoryadministrator" {

  triggers = {
    always_run = timestamp()
  }

//  provisioner "local-exec" {
//     interpreter = ["/bin/bash", "-c"]
//     command = templatefile("${path.module}/script/azcmd.sh.tmpl",
//     {
//        resource_group     = var.resource_group_name
//        sql_server_name    = azurerm_mssql_server.sqlserver.name
//        user_name_lists    = lookup(var.sql_active_directory_administrator[0], "user_name")
//        object_id_list     = lookup(var.sql_active_directory_administrator[0], "object_id")
//      }
//     ) 
//  }
}

resource "azurerm_mssql_server_security_alert_policy" "securityalertpolicy" {
  resource_group_name = var.resource_group_name
  server_name         = azurerm_mssql_server.sqlserver.name
  state               = "Enabled"
}

resource "azurerm_storage_container" "accstoragecontainer" {
  name                  = "accstoragecontainer${var.location}"
  storage_account_name  = data.azurerm_storage_account.storageaccount.name
  container_access_type = "private"
}

resource "azurerm_mssql_server_vulnerability_assessment" "vulnerabilityassessment" {
  server_security_alert_policy_id = azurerm_mssql_server_security_alert_policy.securityalertpolicy.id
  storage_container_path          = "${data.azurerm_storage_account.storageaccount.primary_blob_endpoint}${azurerm_storage_container.accstoragecontainer.name}/"
  storage_account_access_key      = data.azurerm_storage_account.storageaccount.primary_access_key

  recurring_scans {
    enabled                   = true
    email_subscription_admins = true
    emails = var.vulnerability_assessment_emails
  }
  #depends_on = [azurerm_role_assignment.vnet_permission_for_sql_to_storage_account]
}
#
#resource "azurerm_role_assignment" "vnet_permission_for_sql_to_storage_account" {
#  scope                = data.azurerm_storage_account.storageaccount.id
#  role_definition_name = "Contributor"
#  principal_id         = azurerm_mssql_server.sqlserver.identity.0.principal_id
#   }
