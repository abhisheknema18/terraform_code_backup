resource "azurerm_firewall" "this" {
  
  dns_servers             = var.dns_servers
  firewall_policy_id      = azurerm_firewall_policy.this.id
  location                = var.location
  name                    = var.name
  private_ip_ranges       = var.private_ip_ranges
  resource_group_name     = var.resource_group_name
  sku_name                = var.sku_name
  sku_tier                = var.sku_tier
  tags                    = var.tags
  threat_intel_mode       = var.threat_intel_mode
  zones                   = var.zones

  dynamic "ip_configuration" {
    for_each = var.ip_configuration
    content {
      name = ip_configuration.value["name"]
      public_ip_address_id = ip_configuration.value["public_ip_address_id"]
      subnet_id = ip_configuration.value["subnet_id"]
    }
  }

  dynamic "management_ip_configuration" {
    for_each = var.management_ip_configuration
    content {
      name = management_ip_configuration.value["name"]
      public_ip_address_id = management_ip_configuration.value["public_ip_address_id"]
      subnet_id = management_ip_configuration.value["subnet_id"]
    }
  }

  dynamic "timeouts" {
    for_each = var.timeouts
    content {
      create = timeouts.value["create"]
      delete = timeouts.value["delete"]
      read = timeouts.value["read"]
      update = timeouts.value["update"]
    }
  }

  dynamic "virtual_hub" {
    for_each = var.virtual_hub
    content {
      public_ip_count = virtual_hub.value["public_ip_count"]
      virtual_hub_id = virtual_hub.value["virtual_hub_id"]
    }
  }

  depends_on = [azurerm_public_ip.this, azurerm_firewall_policy.this]
}

resource "azurerm_firewall_policy" "this" {
  base_policy_id            = var.base_policy_id
  location                  = var.location
  name                      = var.policy_name
  resource_group_name       = var.resource_group_name
  sku                       = var.sku
  tags                      = var.tags
  threat_intelligence_mode  = var.threat_intelligence_mode

  dynamic "dns" {
    for_each = var.dns
    content {
      network_rule_fqdn_enabled = dns.value["network_rule_fqdn_enabled"]
      proxy_enabled = dns.value["proxy_enabled"]
      servers = dns.value["servers"]
    }
  }

  dynamic "threat_intelligence_allowlist" {
    for_each = var.threat_intelligence_allowlist
    content {
      fqdns = threat_intelligence_allowlist.value["fqdns"]
      ip_addresses = threat_intelligence_allowlist.value["ip_addresses"]
    }
  }

  dynamic "timeouts" {
    for_each = var.policy_timeouts
    content {
      create = timeouts.value["create"]
      delete = timeouts.value["delete"]
      read = timeouts.value["read"]
      update = timeouts.value["update"]
    }
  }

}

resource "azurerm_public_ip" "this" {
  count               = var.nb_public_ip
  name                = "pip-${var.name}-${format("%02s", count.index + 1)}"
  resource_group_name = var.resource_group_name
  location            = var.location
  allocation_method   = var.allocation_method
  sku                 = var.public_ip_sku
  domain_name_label   = element(var.public_ip_dns, count.index)
}
