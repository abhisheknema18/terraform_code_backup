# identifiers
##################Firewall Variable#############
variable "tenant" {
  type    = string
  
}

variable "fwp_name" {
  type    = string

}

variable "fwp_resource_group_name" {
  type    = string

}

variable "fwp_collection_group_priority" {
  type    = number

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
}

variable "ipgroup_cidrs" {

  type        = list(set(string))

}

variable "ipgroup_names" {
  description = "(required)"
  type        = list(string)

}

variable "hub_subscription_id" {
  type = string

}