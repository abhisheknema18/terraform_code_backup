module "azurerm_firewall_policy_rule_collection_group" {
  source = "../modules/firewallcollection"

  firewall_policy_id          = data.azurerm_firewall_policy.fw_policy.id
  name                        = "${var.tenant}RuleCollectionGroup"
  priority                    = var.fwp_collection_group_priority
  ipgroup_resource_group_name = data.azurerm_resource_group.shared.name

  application_rule_collection = var.application_rule_collection
  nat_rule_collection         = var.nat_rule_collection
  network_rule_collection     = var.network_rule_collection

  depends_on = [module.ipgroups]
}