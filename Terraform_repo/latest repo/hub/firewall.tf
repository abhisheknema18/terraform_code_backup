module "firewall" {
  source              = "../modules/firewall/"

  name                    = module.naming_shared.firewall.name
  location                = azurerm_resource_group.shared.location
  resource_group_name     = azurerm_resource_group.shared.name
  nb_public_ip            = length(var.public_ip_conf_name)
  policy_name             = module.naming_shared.firewall_policy.name

  ip_configuration = [{
    name                 = var.public_ip_conf_name[0]
    public_ip_address_id = module.firewall.public_ip_id[0]
    subnet_id            = module.network.vnet_subnets[0]
  }]
}