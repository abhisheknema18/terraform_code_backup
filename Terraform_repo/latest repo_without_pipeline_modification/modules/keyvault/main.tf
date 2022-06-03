resource "azurerm_key_vault" "this" {

  enable_rbac_authorization       = var.enable_rbac_authorization
  enabled_for_deployment          = var.enabled_for_deployment
  enabled_for_disk_encryption     = var.enabled_for_disk_encryption
  enabled_for_template_deployment = var.enabled_for_template_deployment
  location                        = var.location
  name                            = var.name
  purge_protection_enabled        = var.purge_protection_enabled
  resource_group_name             = var.resource_group_name
  sku_name                        = var.sku_name
  soft_delete_retention_days      = var.soft_delete_retention_days
  tags                            = var.tags
  tenant_id                       = var.tenant_id

  dynamic "contact" {
    for_each = var.contact
    content {
      email = contact.value["email"]
      name  = contact.value["name"]
      phone = contact.value["phone"]
    }
  }
  dynamic "network_acls" {
    for_each = var.network_acls
    content {
      bypass                     = network_acls.value["bypass"]
      default_action             = network_acls.value["default_action"]
      ip_rules                    = network_acls.value["ip_rules"]
      virtual_network_subnet_ids = network_acls.value["virtual_network_subnet_ids"]
    }
  }


  dynamic "timeouts" {
    for_each = var.timeouts
    content {
      create = timeouts.value["create"]
      delete = timeouts.value["delete"]
      read   = timeouts.value["read"]
      update = timeouts.value["update"]
    }
  }

}

# Create a Default Azure Key Vault access policy with Admin permissions
# This policy must be kept for a proper run of the "destroy" process
resource "azurerm_key_vault_access_policy" "default_policy" {
  key_vault_id              = azurerm_key_vault.this.id
  tenant_id                 = var.tenant_id
  object_id                 = var.full_access_object_id
  key_permissions           = var.kv_key_permissions_full
  secret_permissions        = var.kv_secret_permissions_full
  certificate_permissions   = var.kv_certificate_permissions_full
  storage_permissions       = var.kv_storage_permissions_full

  lifecycle {
    create_before_destroy = true
  }
}

resource "azurerm_key_vault_access_policy" "policy" {
  for_each                = var.policies
  key_vault_id            = azurerm_key_vault.this.id
  tenant_id               = lookup(each.value, "tenant_id")
  object_id               = lookup(each.value, "object_id")
  key_permissions         = lookup(each.value, "key_permissions")
  secret_permissions      = lookup(each.value, "secret_permissions")
  certificate_permissions = lookup(each.value, "certificate_permissions")
  storage_permissions     = lookup(each.value, "storage_permissions")
}

resource "azurerm_key_vault_secret" "this" {
  count = length(var.secrets_map)
  name         = keys(var.secrets_map)[count.index]
  value        = values(var.secrets_map)[count.index]
  key_vault_id = azurerm_key_vault.this.id

  depends_on = [
    azurerm_key_vault.this,
    azurerm_key_vault_access_policy.default_policy
  ]
}





locals {
  diag_resource_list = var.diagnostics != null ? split("/", var.diagnostics.destination) : []
  parsed_diag = var.diagnostics != null ? {
    log_analytics_id   = contains(local.diag_resource_list, "Microsoft.OperationalInsights") ? var.diagnostics.destination : null
    metric             = var.diagnostics.metrics
    log                = var.diagnostics.logs
  } : {
    log_analytics_id   = null
    metric             = []
    log                = []
  }
}
resource "azurerm_monitor_diagnostic_setting" "this" {
  count                          = var.diagnostics != null ? 1 : 0
  name                           = "${var.name}-kv-diag"
  target_resource_id             = azurerm_key_vault.this.id
  log_analytics_workspace_id     = local.parsed_diag.log_analytics_id

  dynamic "log" {
    for_each = var.diagnostics.logs
    content {
      category = log.value
      enabled  = contains(local.parsed_diag.log, "all") || contains(local.parsed_diag.log, log.value)

      retention_policy {
        enabled = false
        days    = 0
      }
    }
  }

  dynamic "metric" {
    for_each = var.diagnostics.metrics
    content {
      category = metric.value
      enabled  = contains(local.parsed_diag.metric, "all") || contains(local.parsed_diag.metric, metric.value)

      retention_policy {
        enabled = false
        days    = 0
      }
    }
  }
}
