# identifiers
variable "env" {
  type    = string
}

variable "region" {
  type    = string
}

variable "region_shared" {
  type    = string
}

variable "app" {
  type    = string
}

variable "network_address_spaces" {
  type    = list(string)
}

variable "network_subnet_prefixes" {
  type    = list(string)
}

variable "network_subnet_names" {
  type    = list(string)
}

variable "public_ip_conf_name" {
  type        = list(string)
}

variable "threat_intel_mode" {
  type        = string
}

variable "firewall_rule_collection_group_name" {
  type        = string
}

variable "firewall_rule_collection_group_priority" {
  type        = string
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
}

variable "application_rule_collection" {
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
}

variable "nat_rule_collection" {
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
}

variable "ipgroup_cidrs" {
  type        = list(set(string))
}

variable "ipgroup_names" {
  description = "(required)"
  type        = list(string)
}

variable "bastion_name" {
  type        = string
}

variable "bastion_public_ip_conf_name" {
  type        = string
}

variable "bastion_public_ip_name" {
  type        = string
}

variable "bastion_tags" {
  type = map(string)
}

variable "bastion_public_ip_allocation_method" {
  type        = string
}

variable "bastion_public_ip_sku" {
  type        = string
}

variable "bastion_public_ip_dns" {
  type        = string
}