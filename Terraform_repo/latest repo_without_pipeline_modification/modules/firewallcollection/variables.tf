variable "firewall_policy_id" {
  description = "(required)"
  type        = string
}

variable "name" {
  description = "(required)"
  type        = string
}

variable "priority" {
  description = "(required)"
  type        = number
}

variable "ipgroup_resource_group_name" {
  description = "(required)"
  type        = string
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
  default = []
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
  default = []
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
  default = []
}

variable "timeouts" {
  type = set(object(
  {
    create = string
    delete = string
    read   = string
    update = string
  }
  ))
  default = []
}