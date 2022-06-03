module "sa_sql_audit" {
  source                = "../modules/storage"

    name                = format("%ssau", module.naming.storage_account.name)
    resource_group_name = azurerm_resource_group.global.name
    location            = azurerm_resource_group.global.location

    diagnostics = {
        destination     = data.azurerm_log_analytics_workspace.platform.id
        logs            = var.sa_sql_audit_diagnostics_logs
        metrics         = var.sa_sql_audit_diagnostics_metrics
    }
}