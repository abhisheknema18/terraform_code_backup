module "elasticpool" {
  source                      = "../modules/elasticpool"
  sql_server_name             = module.sqlserver.name
  resource_group_name         = azurerm_resource_group.global.name
  location                    = azurerm_resource_group.global.location
  sql_elasticpool_name        = module.naming.sql_elasticpool.name 
  sql_elasticpool_dtu         = var.ep_sql_elasticpool_dtu
  sql_elasticpool_edition     = var.ep_sql_elasticpool_edition
  sql_elasticpool_db_dtu_min  = var.ep_sql_elasticpool_db_dtu_min
  sql_elasticpool_db_dtu_max  = var.ep_sql_elasticpool_db_dtu_max
  sql_elasticpool_max_size_gb = var.ep_sql_elasticpool_max_size_gb
  sql_elasticpool_sku_name    = var.ep_sql_elasticpool_sku_name
  diagnostics = {
    name            = "primary-${module.naming.sql_elasticpool.name}-sqlep-diag"
    destination     = data.azurerm_log_analytics_workspace.platform.id
    logs            = var.elasticpool_audit_diagnostics_logs
    metrics         = var.elasticpool_audit_diagnostics_metrics
  }
  depends_on = [module.sqlserver, module.sqlserver_sec]
}

module "elasticpool_sec" {
  source                      = "../modules/elasticpool"
  sql_server_name             = module.sqlserver_sec.name
  resource_group_name         = azurerm_resource_group.global.name
  location                    = var.region_sec
  sql_elasticpool_name        = module.naming.sql_elasticpool.name
  sql_elasticpool_dtu         = var.ep_sql_elasticpool_dtu
  sql_elasticpool_edition     = var.ep_sql_elasticpool_edition
  sql_elasticpool_db_dtu_min  = var.ep_sql_elasticpool_db_dtu_min
  sql_elasticpool_db_dtu_max  = var.ep_sql_elasticpool_db_dtu_max
  sql_elasticpool_max_size_gb = var.ep_sql_elasticpool_max_size_gb
  sql_elasticpool_sku_name    = var.ep_sql_elasticpool_sku_name
  diagnostics = {
    name            = "secondary-${module.naming.sql_elasticpool.name}-sqlep-diag"
    destination     = data.azurerm_log_analytics_workspace.platform.id
    logs            = var.elasticpool_audit_diagnostics_logs
    metrics         = var.elasticpool_audit_diagnostics_metrics
  }
  depends_on = [module.sqlserver, module.sqlserver_sec]
}