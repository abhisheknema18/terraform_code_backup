variable "enable_http2" {
  description = "(optional)"
  type        = bool
  default     = false
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

variable "resource_group_name" {
  description = "(required)"
  type        = string
}

variable "tags" {
  description = "(optional)"
  type        = map(string)
  default     = null
}

variable "zones" {
  description = "(optional)"
  type        = list(string)
  default     = null
}

variable "authentication_certificate" {
  description = "nested block: NestingList, min items: 0, max items: 0"
  type = set(object(
  {
    data = string
    id   = string
    name = string
  }
  ))
  default = []
}

variable "autoscale_configuration" {
  description = "nested block: NestingList, min items: 0, max items: 1"
  type = set(object(
  {
    max_capacity = number
    min_capacity = number
  }
  ))
  default = []
}

variable "backend_address_pool" {
  description = "nested block: NestingList, min items: 1, max items: 0"
  type = set(object(
  {
    fqdns        = list(string)
    ip_addresses = list(string)
    name         = string
  }
  ))
}


variable "backend_http_settings" {

   type = set(object(
 {
    cookie_based_affinity               = string
    host_name                           = string
    #id                                  = string
    name                                = string
    path                                = string
    pick_host_name_from_backend_address = bool
    port                                = number
    probe_id                            = string
    probe_name                          = string
    protocol                            = string
    request_timeout                     = number

  }
  ))
}


variable "frontend_ip_configuration" {
  description = "nested block: NestingList, min items: 1, max items: 0"
  type = set(object(
  {
    name                          = string
    public_ip_address_id          = string
     }
  ))
}

variable "frontend_port" {
  description = "nested block: NestingSet, min items: 1, max items: 0"
  type = set(object(
  {
    name = string
    port = number
  }
  ))
}

variable "gateway_ip_configuration" {
  description = "nested block: NestingList, min items: 1, max items: 2"
  type = set(object(
  {
    #id        = string
    name      = string
    subnet_id = string
  }
  ))
}

variable "http_listener" {
  description = "nested block: NestingList, min items: 1, max items: 0"
  type = set(object(
  {
    frontend_ip_configuration_name = string
    frontend_port_name             = string
    host_name                      = string
    name                           = string
    protocol                       = string
    require_sni                    = bool
    ssl_certificate_name           = string
  }
  ))
}

variable "identity" {
  description = "nested block: NestingList, min items: 0, max items: 1"
  type = set(object(
  {
    identity_ids = list(string)
    type         = string
  }
  ))
  default = []
}

variable "probe" {
  description = "nested block: NestingList, min items: 0, max items: 0"
  type = set(object(
  {
    host                                      = string
    interval                                  = number
    name                                      = string
    path                                      = string
    pick_host_name_from_backend_http_settings = bool
    port                                      = number
    protocol                                  = string
    timeout                                   = number
    unhealthy_threshold                       = number
  }
  ))
  default = []
}

variable "redirect_configuration" {
  description = "nested block: NestingSet, min items: 0, max items: 0"
  type = set(object(
  {
    id                   = string
    include_path         = bool
    include_query_string = bool
    name                 = string
    redirect_type        = string
    target_listener_id   = string
    target_listener_name = string
    target_url           = string
  }
  ))
  default = []
}

variable "request_routing_rule" {
  description = "nested block: NestingSet, min items: 1, max items: 0"
  type = set(object(
  {
    backend_address_pool_name   = string
    backend_http_settings_name  = string
    http_listener_name          = string
    name                        = string
    rule_type                   = string
    url_path_map_name           = string
  }
  ))
}

variable "rewrite_rule_set" {
  description = "nested block: NestingList, min items: 0, max items: 0"
  type = set(object(
  {
    id   = string
    name = string
    rewrite_rule = list(object(
    {
      condition = list(object(
      {
        ignore_case = bool
        negate      = bool
        pattern     = string
        variable    = string
      }
      ))
      name = string
      request_header_configuration = list(object(
      {
        header_name  = string
        header_value = string
      }
      ))
      response_header_configuration = list(object(
      {
        header_name  = string
        header_value = string
      }
      ))
      rule_sequence = number
      url = list(object(
      {
        path         = string
        query_string = string
        reroute      = bool
      }
      ))
    }
    ))
  }
  ))
  default = []
}

variable "sku" {
  description = "nested block: NestingList, min items: 1, max items: 1"
  type = set(object(
  {
    capacity = number
    name     = string
    tier     = string
  }
  ))
}


variable "ssl_certificate" {
  description = "List of SSL certificates data for Application gateway"
  type = list(object({
    name                = string
    data                = string
    password            = string

  }))
  default = []
}

variable "ssl_policy" {
  description = "nested block: NestingList, min items: 0, max items: 0"
  type = set(object(
  {
    cipher_suites        = list(string)
    disabled_protocols   = list(string)
    min_protocol_version = string
    policy_name          = string
    policy_type          = string
  }
  ))
  default = []
}

variable "timeouts" {
  description = "nested block: NestingSingle, min items: 0, max items: 0"
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

variable "trusted_root_certificate" {
  description = "nested block: NestingList, min items: 0, max items: 0"
  type = set(object(
  {
    data = string
    id   = string
    name = string
  }
  ))
  default = []
}

variable "url_path_map" {
  description = "nested block: NestingList, min items: 0, max items: 0"
  type = set(object(
  {
    default_backend_address_pool_name   = string
    default_backend_http_settings_name  = string
    name                                = string
    path_rule  = list(object(
    {

      backend_address_pool_name   = string

      backend_http_settings_name  = string
      name                        = string
      paths                       = list(string)
    }
    ))
  }
  ))
  default = []
}

variable "waf_configuration" {
  description = "nested block: NestingList, min items: 0, max items: 1"
  type = set(object(
  {
    disabled_rule_group = list(object(
    {
      rule_group_name = string
      rules           = list(number)
    }
    ))
    enabled = bool
    firewall_mode            = string
    rule_set_type            = string
    rule_set_version         = string
  }
  ))
  default = []
}

variable "diagnostics" {
  type = object({
    destination   = string
    logs          = list(string)
    metrics       = list(string)
  })
  default = null
}
