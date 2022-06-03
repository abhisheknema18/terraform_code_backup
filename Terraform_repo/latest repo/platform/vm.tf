module "ado-build-agent-vm" {
  source              =  "../modules/vm/linux"
  count                =var.ado_build_agent_vm_nb_instances == "" ? 0 : var.ado_build_agent_vm_nb_instances
  resource_group_name = azurerm_resource_group.this.name
  location            = azurerm_resource_group.this.location
  nb_instances        = var.ado_build_agent_vm_nb_instances
  vm_hostname         = "ag${format("%02s", count.index + 1)}linsrvuks-build"
  admin_password      = var.vm_admin_password
  vm_os_simple        = var.linux_vm_os_simple
  nb_public_ip        = var.nb_public_ip
  vnet_subnet_id      = module.network.vnet_subnets[2]
  delete_os_disk_on_termination = var.delete_os_disk_on_termination
  delete_data_disks_on_termination = var.delete_data_disks_on_termination
  availability_set_id = module.availability_set_ado.id
  nb_data_disk        = var.nb_data_disk_ado
  data_disk_size_gb   = var.data_disk_size_gb
  data_sa_type        = var.data_sa_type
 depends_on = [module.availability_set_ado,azurerm_log_analytics_workspace.this]
}
module "ado-deploy-agent-vm" {
  source              =  "../modules/vm/linux"
  count                =var.ado_deploy_agent_vm_nb_instances == "" ? 0 : var.ado_deploy_agent_vm_nb_instances
  resource_group_name = azurerm_resource_group.this.name
  location            = azurerm_resource_group.this.location
  nb_instances        = var.ado_deploy_agent_vm_nb_instances
  vm_hostname         = "ag${format("%02s", count.index + 1)}linsrvuks-deploy"
  admin_password      = var.vm_admin_password
  vm_os_simple        = var.linux_vm_os_simple
  nb_public_ip        = var.nb_public_ip
  vnet_subnet_id      = module.network.vnet_subnets[2]
  delete_os_disk_on_termination = var.delete_os_disk_on_termination
  delete_data_disks_on_termination = var.delete_data_disks_on_termination
  availability_set_id = module.availability_set_ado.id
  nb_data_disk        = var.nb_data_disk_ado
  data_disk_size_gb   = var.data_disk_size_gb
  data_sa_type        = var.data_sa_type
  depends_on = [module.availability_set_ado,azurerm_log_analytics_workspace.this]
}

module "operation-vm" {
  source               =  "../modules/vm/windows"
  count                =var.windows_nb_instances == "" ? 0 : var.windows_nb_instances
  resource_group_name = azurerm_resource_group.this.name
  location            = azurerm_resource_group.this.location
  nb_instances        = var.windows_nb_instances
  vm_hostname         = "ops${format("%02s", count.index + 1)}winsrvuks"
  admin_password      = var.vm_admin_password
  vm_os_simple        = var.window_vm_os_simple
  nb_public_ip        = var.nb_public_ip
  vnet_subnet_id      = module.network.vnet_subnets[3]
  delete_os_disk_on_termination = var.delete_os_disk_on_termination
  availability_set_id = module.availability_set_ops.id
  type_handler_version = var.IaaSAntimalware_type_handler_version
#  diagnostics = {
#    destination     = azurerm_log_analytics_workspace.this[0].id
#    logs            = var.vm_audit_diagnostics_logs
#    metrics         = var.vm_audit_diagnostics_metrics
#  }
  depends_on = [module.availability_set_ops,azurerm_log_analytics_workspace.this]
}

