variable "isMultiregion" {
  type    = bool
  }


##############################Application gateway########################'
variable "env" {
  type    = string
}

variable "region" {
  type    = string
}

variable "region_sec" {
  type    = string
}

###### customer type  Shared  OR  enterprise customer########
variable "customer_type" {
  type    = string
}

variable "shared" {
  type    = string
}
variable "app" {
  type    = string
}


########################################################### Network & Peering ##############
variable "hub_vnet_name" {
  type    = string
 }

variable "hub_vnet_resource_group" {
  type    = string
}

variable "hub_subscription_id" {
  type = string
 }

variable "plateform_subscription_id" {
  type = string
}

variable "network_address_spaces" {
  type = list(string)

}

variable "network_address_spaces_ukwest" {
  type = list(string)

}

variable "network_subnet_prefixes" {
  type = list(string)
   
}

variable "network_subnet_prefixes_ukwest" {
  type = list(string)
   
}

variable "network_subnet_names" {
  type = list(string)
   
}

variable "virtual_network_peering_appgateway_to_firewall_name" {
  type = string
   
}

variable "virtual_network_peering_appgateway_to_firewall_name_ukwest" {
  type = string
   
}

variable "virtual_network_peering_firewall_to_appgateway" {
  type = string
   
}

variable "virtual_network_peering_firewall_to_appgateway_ukwest" {
  type = string
   
}

variable "routetable_disable_bgp_route_propagation" {
  type    = bool
   
}
variable "routetable_enable_force_tunneling" {
  type    = bool
   
}
variable "routetable_next_hop_type" {
  type    = string
   
}
variable "routetable_next_hop_in_ip_address" {
  type = string
   
}
variable "route_address_prefix" {
  type    = string
   
}
variable "route_address_prefix_ukwest" {
  type    = string
   
}
############################################# Application Gateway ####################################

variable "agw_sku" {
  type = set(object(
  {
    capacity = number
    name     = string
    tier     = string
  }
  ))
 
}

variable "enable_http2" {
  type        = bool
    
}

variable "firewall_policy_id" {
  type        = string
    
}

variable "agw_azurerm_public_ip_name" {
  type = string
   }

variable "allocation_method" {
  type        = string
    
}

variable "public_ip_sku" {
  type        = string
    
}

variable "public_ip_availability_zone" {
  type        = string
    
}

variable "frontend_port" {
  type = set(object(
  {
    name = string
    port = number
  }
  ))

}

variable "backend_address_pool" {
  type = set(object(
  {
    fqdns        = list(string)
    ip_addresses = list(string)
    name         = string
  }
  ))
}

variable "backend_address_pool_ukwest" {
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
    id                                  = string
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

variable "http_listener" {
  type = set(object(
  {

    frontend_ip_configuration_name = string
    frontend_port_name             = string
    host_name                      = string
    name                           = string
    protocol                       = string
    require_sni                    = bool
    ssl_certificate_id             = string
    ssl_certificate_name           = string
  }
  ))

}

variable "http_listener_ukwest" {
  type = set(object(
  {

    frontend_ip_configuration_name = string
    frontend_port_name             = string
    host_name                      = string
    name                           = string
    protocol                       = string
    require_sni                    = bool
    ssl_certificate_id             = string
    ssl_certificate_name           = string
  }
  ))

}


variable "ssl_certificate" {
  description = "nested block: NestingList, min items: 0, max items: 0"
  type = list(object(
  {
    data     = string
    name     = string
    password = string
  }
  ))

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

}

variable "request_routing_rule" {
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


variable "url_path_map" {

  type = set(object(
  {

    default_backend_address_pool_name   = string
    default_backend_http_settings_name  = string
    name                                = string
    path_rule = list(object(
    {
      backend_address_pool_name   = string
      backend_http_settings_name  = string
      name                        = string
      paths                       = list(string)

    }
    ))
  }
  ))
}

variable "waf_configuration" {
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

}


####################################### Traffic manager End point ##################

variable "type" {
  
  type        = string
   
}
 ###########################################Traffic manager profile ##########################

variable "profile_status" {
  type        = string
    
}

variable "tags" {
  type        = map(string)
    
}

variable "traffic_routing_method" {
  type        = string
    
}

variable "traffic_view_enabled" {
  type        = bool
    
}

variable "dns_config" {
  description = "nested block: NestingList, min items: 1, max items: 1"
  type = list(object(
  {
    relative_name = string
    ttl           = number
  }
  ))

}

variable "monitor_config" {
  description = "nested block: NestingList, min items: 1, max items: 1"
  type = set(object(
  {
    expected_status_code_ranges  = list(string)
    path                         = string
    port                         = number
    protocol                     = string
    timeout_in_seconds           = number
    tolerated_number_of_failures = number
  }
  ))

}

variable "log_analytics_workspace_name" {
  type        = string
    
}

variable "platform_shared_rg_name" {
  type    = string
   
}

variable "appgateway_audit_diagnostics_logs" {
  type    = list(string)
   
}
variable "appgateway_audit_diagnostics_metrics" {
  type    = list(string)
   
}

variable "traffic_manager_audit_diagnostics_logs" {
  type    = list(string)
}
variable "traffic_manager_diagnostics_metrics" {
  type    = list(string)
}

variable "ipgroup_cidrs" {
    type        = list(set(string))
}

variable "ipgroup_names" {
  type        = list(string)
}

variable "fwp_resource_group_name" {
  type    = string
  }