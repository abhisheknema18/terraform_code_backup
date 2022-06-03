variable "dns_servers" {
  description = "(optional)"
  type        = list(string)
  default     = null
}

variable "firewall_policy_id" {
  description = "(optional)"
  type        = string
  default     = null
}

variable "location" {
  description = "(required)"
  type        = string
}

variable "name" {
  description = "(required)"
  type        = string
}

variable "private_ip_ranges" {
  description = "(optional)"
  type        = set(string)
  default     = null
}

variable "resource_group_name" {
  description = "(required)"
  type        = string
}

variable "sku_name" {
  description = "(optional)"
  type        = string
  default     = null
}

variable "sku_tier" {
  description = "(optional)"
  type        = string
  default     = null
}

variable "tags" {
  description = "(optional)"
  type        = map(string)
  default     = null
}

variable "threat_intel_mode" {
  description = "(optional)"
  type        = string
  default     = null
}

variable "zones" {
  description = "(optional)"
  type        = list(string)
  default     = null
}

variable "ip_configuration" {
  type = set(object(
  {
    name                 = string
    public_ip_address_id = string
    subnet_id            = string
  }
  ))
  default = []
}

variable "management_ip_configuration" {
  type = set(object(
  {
    name                 = string
    private_ip_address   = string
    public_ip_address_id = string
    subnet_id            = string
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

variable "virtual_hub" {
  type = set(object(
  {
    private_ip_address  = string
    public_ip_addresses = list(string)
    public_ip_count     = number
    virtual_hub_id      = string
  }
  ))
  default = []
}

variable "allocation_method" {
  description = "(optional)"
  type        = string
  default     = "Static"
}

variable "public_ip_sku" {
  description = "(optional)"
  type        = string
  default     = "Standard"
}

variable "nb_public_ip" {
  description = "(optional)"
  type        = number
  default     = 1
}

variable "public_ip_dns" {
  description = "(optional)"
  type        = list(string)
  default     = [null]
}

variable "base_policy_id" {
  description = "(optional)"
  type        = string
  default     = null
}

variable "policy_name" {
  description = "(required)"
  type        = string
}

variable "sku" {
  description = "(optional)"
  type        = string
  default     = null
}

variable "threat_intelligence_mode" {
  description = "(optional)"
  type        = string
  default     = null
}

variable "dns" {
  type = set(object(
  {
    network_rule_fqdn_enabled = bool
    proxy_enabled             = bool
    servers                   = set(string)
  }
  ))
  default = []
}

variable "threat_intelligence_allowlist" {
  type = set(object(
  {
    fqdns        = set(string)
    ip_addresses = set(string)
  }
  ))
  default = []
}

variable "policy_timeouts" {
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
