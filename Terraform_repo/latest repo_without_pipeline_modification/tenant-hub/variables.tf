# identifiers
##################Firewall Variable#############
variable "tenant" {
  type    = string
  default = "Dev"
}

variable "fwp_name" {
  type    = string
  default = "fwp-hub-dev-shared"
}

variable "fwp_resource_group_name" {
  type    = string
  default = "rg-hub-dev-shared"
}

variable "fwp_collection_group_priority" {
  type    = number
  default = 5000
}

variable "nat_rule_collection" {
  description = "nested block: NestingSet, min items: 0, max items: 0"
  type = set(object(
  {
    action   = string
    name     = string
    priority = number
    rule = set(object(
    {
      destination_address = string
      destination_ports   = set(string)
      name                = string
      protocols           = set(string)
      source_addresses    = set(string)
      source_ip_groups    = set(string)
      translated_address  = string
      translated_port     = number
    }
    ))
  }
  ))
  default = [{
    action   = "Dnat"
    name     = "Dev-DNATRuleCollection"
    priority = 5100
    rule = [{
      destination_address = "20.108.90.147"
      destination_ports   = ["3300"]
      name                = "Dev-SB-RDP"
      protocols           = ["TCP"]
      source_addresses    = ["*"]
      source_ip_groups    = []
      translated_address  = "172.16.4.36"
      translated_port     = 3389
    }]
  }]
}

variable "network_rule_collection" {
  type = set(object(
  {
    action   = string
    name     = string
    priority = number
    rule = set(object(
    {
      destination_addresses = set(string)
      destination_fqdns     = set(string)
      destination_ip_groups = set(string)
      destination_ports     = set(string)
      name                  = string
      protocols             = set(string)
      source_addresses      = set(string)
      source_ip_groups      = set(string)
    }
    ))
  }
  ))
  default = [{
    action   = "Allow"
    name     = "Dev-NetworkRuleCollection"
    priority = 5200
    rule = [{
      destination_addresses = []
      destination_fqdns     = []
      destination_ip_groups = ["ipgroup-dev-all","ipgroup-hub-lower-ag"]
      destination_ports     = ["443"]
      name                  = "HTTPS"
      protocols             = ["TCP"]
      source_addresses      = []
      source_ip_groups      = ["ipgroup-dev-all","ipgroup-hub-lower-ag"]
    },{
      destination_addresses = []
      destination_fqdns     = []
      destination_ip_groups = ["ipgroup-dev-all"]
      destination_ports     = ["*"]
      name                  = "PING"
      protocols             = ["ICMP"]
      source_addresses      = []
      source_ip_groups      = ["ipgroup-dev-all"]
    },
      {
        destination_addresses = ["AzureContainerRegistry"]
        destination_fqdns     = []
        destination_ip_groups = []
        destination_ports     = ["*"]
        name                  = "ACR"
        protocols             = ["TCP"]
        source_addresses      = []
        source_ip_groups      = ["ipgroup-dev-all"]
      }
    ]
  }]
}

variable "application_rule_collection" {
  description = "nested block: NestingSet, min items: 0, max items: 0"
  type = set(object(
  {
    action   = string
    name     = string
    priority = number
    rule = set(object(
    {
      destination_fqdn_tags = set(string)
      destination_fqdns     = set(string)
      web_categories        = set(string)
      name                  = string
      protocols = set(object(
      {
        port = number
        type = string
      }
      ))
      source_addresses = set(string)
      source_ip_groups = set(string)
    }
    ))
  }
  ))
  default = [{
    action   = "Allow"
    name     = "Dev-ApplicationRuleCollection"
    priority = 5300
    rule = [{
      destination_fqdn_tags = []
      destination_fqdns     = ["mcamt.eu.auth0.com"]
      web_categories        = []
      name                  = "IdentityProvider"
      protocols = [{
        port = 443
        type = "Https"
      }]
      source_addresses = []
      source_ip_groups = ["ipgroup-dev-all"]
    }, {
      destination_fqdn_tags = []
      destination_fqdns     = ["*.dev.azure.com"]
      web_categories        = []
      name                  = "Web"
      protocols = [{
        port = 443
        type = "Https"
      }]
      source_addresses = []
      source_ip_groups = ["ipgroup-dev-all"]
    },{
      destination_fqdn_tags = []
      destination_fqdns     = ["v1.fieldsmarttest.com"]
      web_categories        = []
      name                  = "Web-domain"
      protocols = [{
        port = 443
        type = "Https"
      }]
      source_addresses = []
      source_ip_groups = ["ipgroup-dev-all"]
    }]
  }]
}

variable "ipgroup_cidrs" {
  description = "(optional)"
  type        = list(set(string))
  default     = [["172.16.8.0/21", "172.16.4.32/27", "10.16.8.0/21", "10.16.4.32/27"]]
}

variable "ipgroup_names" {
  description = "(required)"
  type        = list(string)
  default     = ["ipgroup-dev-all"]
}

variable "hub_subscription_id" {
  type = string
  default = "a95faac2-cab9-4d56-8c6d-fc818f9cf136"
}