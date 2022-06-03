module "azurerm_key_vault" {
  source = "../modules/keyvault"
  count                         = var.isMultiregion     == false ? 1 : 0
  name                          = module.naming.key_vault.name
  location                      = azurerm_resource_group.global.location
  resource_group_name           = azurerm_resource_group.global.name
  sku_name                      = var.kv_sku
  soft_delete_retention_days    = var.kv_soft_delete_retention_days
  tags                          = var.kv_tags
  tenant_id                     = data.azurerm_client_config.current.tenant_id
  full_access_object_id         = data.azurerm_client_config.current.object_id
  secrets_map                   = var.kv_secrets_map
  network_acls = [
    {
      bypass                     = "AzureServices"
      default_action             = "Allow"
      ip_rules                   = ["51.140.48.97"]
      virtual_network_subnet_ids = tolist([data.azurerm_subnet.platform.id,module.network.vnet_subnets[0],data.azurerm_subnet.platform_ado.id])
    },
     ]

  diagnostics = {
    destination     = data.azurerm_log_analytics_workspace.platform.id
    logs            = var.kv_audit_diagnostics_logs
    metrics         = var.kv_audit_diagnostics_metrics
  }

  policies = {
    read = {
      tenant_id               = data.azurerm_client_config.current.tenant_id
      object_id               = module.aks.kubelet_identity_object_id
      key_permissions         = var.kv_key_permissions_read
      secret_permissions      = var.kv_secret_permissions_read
      certificate_permissions = var.kv_certificate_permissions_read
      storage_permissions     = var.kv_storage_permissions_no_access
    }
  }

}



module "azurerm_key_vault_mutilregion" {
  source = "../modules/keyvault"
  count = var.isMultiregion     == false ? 0 : 1
  name                          = module.naming.key_vault.name
  location                      = azurerm_resource_group.global.location
  resource_group_name           = azurerm_resource_group.global.name
  sku_name                      = var.kv_sku
  soft_delete_retention_days    = var.kv_soft_delete_retention_days
  tags                          = var.kv_tags
  tenant_id                     = data.azurerm_client_config.current.tenant_id
  full_access_object_id         = data.azurerm_client_config.current.object_id
  secrets_map                   = var.kv_secrets_map
  network_acls = [
    {
      bypass                     = "AzureServices"
      default_action             = "Allow"
      ip_rules                   = ["51.140.48.97"]
      virtual_network_subnet_ids = tolist([data.azurerm_subnet.platform.id,module.network.vnet_subnets[0],module.network-ukwest[0].vnet_subnets[0],data.azurerm_subnet.platform_ado.id])
    },
  ]

  diagnostics = {
    destination     = data.azurerm_log_analytics_workspace.platform.id
    logs            = var.kv_audit_diagnostics_logs
    metrics         = var.kv_audit_diagnostics_metrics
  }

  policies = {
    read = {
      tenant_id               = data.azurerm_client_config.current.tenant_id
      object_id               = module.aks.kubelet_identity_object_id
      key_permissions         = var.kv_key_permissions_read
      secret_permissions      = var.kv_secret_permissions_read
      certificate_permissions = var.kv_certificate_permissions_read
      storage_permissions     = var.kv_storage_permissions_no_access
    }
  }

}