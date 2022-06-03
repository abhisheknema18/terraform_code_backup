module "availability_set_ado" {
  source = "../modules/vm/availability-set/"

  availability_set_name = format("%s-ado", module.naming.availability_set.name)
  resource_group_name = azurerm_resource_group.this.name
  location = var.region
  managed = var.managed
  platform_fault_domain_count = var.platform_fault_domain_count
  platform_update_domain_count = var.platform_update_domain_count
  tags = {}

  timeouts = [{
    create = null
    delete = null
    read   = null
    update = null
  }]

}

module "availability_set_ops" {
  source = "../modules/vm/availability-set/"

  availability_set_name = format("%s-ops", module.naming.availability_set.name)
  resource_group_name = azurerm_resource_group.this.name
  location = var.region
  managed = var.managed
  platform_fault_domain_count = var.platform_fault_domain_count
  platform_update_domain_count = var.platform_update_domain_count
  tags = {}

  timeouts = [{
    create = null
    delete = null
    read   = null
    update = null
  }]

}