module "ado_nsg"{
  source                = "../modules/nsg/"
  resource_group_name   = azurerm_resource_group.this.name
  location              = azurerm_resource_group.this.location
  security_group_name   = format("%s-ado", module.naming.network_security_group.name)
  source_address_prefix = data.azurerm_virtual_network.hubvnet.address_space
  predefined_rules      = var.nsg_predefined_rules_ado
  depends_on = [module.ado-build-agent-vm, module.ado-deploy-agent-vm]
}

module "ops_nsg"{
  source                = "../modules/nsg/"
  resource_group_name   = azurerm_resource_group.this.name
  location              = azurerm_resource_group.this.location
  security_group_name   = format("%s-ops", module.naming.network_security_group.name)
  source_address_prefix = data.azurerm_virtual_network.hubvnet.address_space
  predefined_rules      = var.nsg_predefined_rules_ops
  depends_on = [module.operation-vm]

}



