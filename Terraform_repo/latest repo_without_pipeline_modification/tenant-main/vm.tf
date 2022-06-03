module "windowsservers" {
  source              = "../modules/vm/windows/"
  count               = var.windows_nb_instances == "" ? 0 : var.windows_nb_instances
  resource_group_name = azurerm_resource_group.global.name
  location            = azurerm_resource_group.global.location
  #is_windows_image    = var.vm_is_windows_image
  nb_instances        = var.windows_nb_instances
  vm_hostname         = "sb${format("%02s", count.index + 1)}winsrvuks"
  admin_password      = var.vm_admin_password
  vm_os_simple        = var.vm_os_simple
  nb_public_ip        = var.vm_number_of_public_ip
  type_handler_version = var.IaaSAntimalware_type_handler_version
  delete_os_disk_on_termination = var.delete_os_disk_on_termination
  vnet_subnet_id      = module.network_global.vnet_subnets[0]
}

