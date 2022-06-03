module "azurerm_storage_share" {
  source = "../modules/fileshare"
  count     = var.isMultiregion     == false ? 1 : 0
  name                        = format("%sfil", module.naming.storage_account.name)
  resource_group_name         = azurerm_resource_group.global.name
  location                    = azurerm_resource_group.global.location
  network_rules = [
    {
      bypass                     = ["AzureServices"]
      default_action             = "Allow"
     ip_rules                   = ["51.140.48.97"]
      virtual_network_subnet_ids =tolist([data.azurerm_subnet.platform.id,module.network.vnet_subnets[0],data.azurerm_subnet.platform_ado.id])
},
]

  diagnostics = {
    destination     = data.azurerm_log_analytics_workspace.platform.id
    logs            = var.sa_file_share_audit_diagnostics_logs
    metrics         = var.sa_file_share_audit_diagnostics_metrics
  }

  file_share_name             = var.fs_name
  quota                       = var.fs_quota
  primary_directories         = var.fs_primary_directories
  secondary_directories       = var.fs_secondary_directories
}


module "azurerm_storage_share_mutliregion" {
  source = "../modules/fileshare"
  count = var.isMultiregion     == false ? 0 : 1
  name                        = format("%sfil", module.naming.storage_account.name)
  resource_group_name         = azurerm_resource_group.global.name
  location                    = azurerm_resource_group.global.location
  network_rules = [
    {
      bypass                     = ["AzureServices"]
      default_action             = "Allow"
      ip_rules                   = ["51.140.48.97"]
      virtual_network_subnet_ids =var.isMultiregion== true ? tolist([data.azurerm_subnet.platform.id,module.network.vnet_subnets[0],data.azurerm_subnet.platform_ado.id]) : tolist([data.azurerm_subnet.platform.id,module.network.vnet_subnets[0],module.network-ukwest[0].vnet_subnets[0],data.azurerm_subnet.platform_ado.id])
    },
  ]

  diagnostics = {
    destination     = data.azurerm_log_analytics_workspace.platform.id
    logs            = var.sa_file_share_audit_diagnostics_logs
    metrics         = var.sa_file_share_audit_diagnostics_metrics
  }

  file_share_name             = var.fs_name
  quota                       = var.fs_quota
  primary_directories         = var.fs_primary_directories
  secondary_directories       = var.fs_secondary_directories
}