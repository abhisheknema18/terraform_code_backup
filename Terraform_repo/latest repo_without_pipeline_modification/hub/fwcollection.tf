module "firewall_rule_collection" {
  source = "../modules/firewallcollection"

  firewall_policy_id            = module.firewall.firewall_policy_id
  name                          = var.firewall_rule_collection_group_name
  priority                      = var.firewall_rule_collection_group_priority
  ipgroup_resource_group_name   = azurerm_resource_group.shared.name
  application_rule_collection   = var.application_rule_collection
  nat_rule_collection           = var.nat_rule_collection
  network_rule_collection       = var.network_rule_collection
  
  depends_on = [module.firewall, module.ipgroups]
}