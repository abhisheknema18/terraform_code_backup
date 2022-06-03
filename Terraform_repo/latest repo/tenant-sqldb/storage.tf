module "sa_sqldb_audit" {
  source                = "../modules/storage"

    name                = format("%sdb", module.naming.storage_account.name)
    resource_group_name = data.azurerm_resource_group.shared.name
    location            = data.azurerm_resource_group.shared.location

    diagnostics = {
        destination     = data.azurerm_log_analytics_workspace.platform.id
        logs            = var.sa_sqldb_audit_diagnostics_logs
        metrics         = var.sa_sqldb_audit_diagnostics_metrics
    }
}

module "sa_sqltd_audit" {
  source                = "../modules/storage"

    name                = format("%std", module.naming.storage_account.name)
    resource_group_name = data.azurerm_resource_group.shared.name
    location            = data.azurerm_resource_group.shared.location

    diagnostics = {
        destination     = data.azurerm_log_analytics_workspace.platform.id
        logs            = var.sa_sqltd_audit_diagnostics_logs
        metrics         = var.sa_sqltd_audit_diagnostics_metrics
    }
}