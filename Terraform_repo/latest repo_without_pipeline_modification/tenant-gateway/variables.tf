variable "isMultiregion" {
  type    = bool
  default = false
}


##############################Application gateway########################'
variable "env" {
  type    = string
  default = "dev"
}

variable "region" {
  type    = string
  default = "uksouth"
}

variable "region_sec" {
  type    = string
  default = "ukwest"
}

###### customer type  Shared  OR  enterprise customer########
variable "customer_type" {
  type    = string
  default = "public"

}
variable "shared" {
  type    = string
  default = "public"

}
variable "app" {
  type    = string
  default = "hub"
}


########################################################### Network & Peering ##############
variable "hub_vnet_name" {
  type    = string
  default = "vnet-hub-dev-shared"
}

variable "hub_vnet_resource_group" {
  type    = string
  default = "rg-hub-dev-shared"
}

variable "hub_subscription_id" {
  type = string
  default = "a95faac2-cab9-4d56-8c6d-fc818f9cf136"
}

variable "plateform_subscription_id" {
  type = string
  default = "1840866c-daf9-45f5-b823-bc446ee9c6b2"
}

variable "network_address_spaces" {
  type = list(string)
  default = ["172.16.1.0/26"]
}

variable "network_address_spaces_ukwest" {
  type = list(string)
  default = ["10.16.1.0/26"]
}

variable "network_subnet_prefixes" {
  type = list(string)
  default = ["172.16.1.0/27"]
}

variable "network_subnet_prefixes_ukwest" {
  type = list(string)
  default = ["10.16.1.0/27"]
}

variable "network_subnet_names" {
  type = list(string)
  default = ["snet-agw"]
}

variable "virtual_network_peering_appgateway_to_firewall_name" {
  type = string
  default = "AppGatewayLowerUksouthToFirewall"
}

variable "virtual_network_peering_appgateway_to_firewall_name_ukwest" {
  type = string
  default = "AppGatewayLowerUkwestToFirewall"
}

variable "virtual_network_peering_firewall_to_appgateway" {
  type = string
  default = "FirewallToAppGatewayLowerUksouth"
}

variable "virtual_network_peering_firewall_to_appgateway_ukwest" {
  type = string
  default = "FirewallToAppGatewayLowerUkwest"
}

variable "routetable_disable_bgp_route_propagation" {
  type    = bool
  default = false
}
variable "routetable_enable_force_tunneling" {
  type    = bool
  default = true
}
variable "routetable_next_hop_type" {
  type    = string
  default = "VirtualAppliance"
}
variable "routetable_next_hop_in_ip_address" {
  type = string
  default = "192.168.42.132"
}
variable "route_address_prefix" {
  type    = string
  default = "172.16.0.0/12"
}
variable "route_address_prefix_ukwest" {
  type    = string
  default = "10.16.0.0/12"
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
  default = [
    {
      capacity = 1
      name     = "WAF_v2"
      tier     = "WAF_v2"
    }]
}

variable "enable_http2" {
  type        = bool
  default     = false
}

variable "firewall_policy_id" {
  type        = string
  default     = null
}

variable "agw_azurerm_public_ip_name" {
  type = string
  default = "appGatewayPublicIP"
}

variable "allocation_method" {
  type        = string
  default     = "Static"
}

variable "public_ip_sku" {
  type        = string
  default     = "Standard"
}

variable "public_ip_availability_zone" {
  type        = string
  default     = "No-Zone"
}
variable "gateway_ip_configuration" {
  type = set(object(
  {
    name      = string
    subnet_id = string
  }
  ))
  default = [{
    name      = ""
    subnet_id = ""
  }]
}

variable "frontend_ip_configuration" {
  type = set(object(
  {
    name                          = string
    public_ip_address_id          = string

  }
  ))
  default = [{
    name                          = ""
    public_ip_address_id          = ""
  }]
}

variable "frontend_port" {
  type = set(object(
  {
    name = string
    port = number
  }
  ))
  default = [{
    name = "httpsPort"
    port = 443
  }
  ]
}

variable "backend_address_pool" {
  type = set(object(
  {
    fqdns        = list(string)
    ip_addresses = list(string)
    name         = string
  }
  ))
  default = [
    {
      fqdns        = ["amt-sybex.com"]
      name         = "AMTSybex"
      ip_addresses = []
    },
    {
      ip_addresses = ["172.16.8.210"]
      name         = "dev"
      fqdns        = []
    },
    {
      ip_addresses = ["172.17.8.210"]
      name         = "qa"
      fqdns        = []
    },
    {
      ip_addresses = ["172.18.8.210"]
      name         = "uat"
      fqdns        = []
    },
    {
      ip_addresses = ["172.19.8.210"]
      name         = "aut"
      fqdns        = []
    }
  ]
}

variable "backend_address_pool_ukwest" {
  type = set(object(
  {
    fqdns        = list(string)
    ip_addresses = list(string)
    name         = string
  }
  ))
  default = [
    {
      fqdns        = ["amt-sybex.com"]
      name         = "AMTSybex"
      ip_addresses = []
    },
    {
      ip_addresses = ["10.16.8.210"]
      name         = "dev"
      fqdns        = []
    },
    {
      ip_addresses = ["10.17.8.210"]
      name         = "qa"
      fqdns        = []
    },
    {
      ip_addresses = ["10.18.8.210"]
      name         = "uat"
      fqdns        = []
    },
    {
      ip_addresses = ["10.19.8.210"]
      name         = "aut"
      fqdns        = []
    }
  ]
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
  default = [
  {
    cookie_based_affinity               = "Disabled"
    host_name                           = "amt.v1.fieldsmarttest.com"
    id                                  = ""
    name                                = "dev-https-settings"
    path                                = "/dev/"
    pick_host_name_from_backend_address = false
    port                                = 443
    probe_id                            = ""
    probe_name                          = "dev-probe"
    protocol                            = "HTTPS"
    request_timeout                     = 20
  },
    {
      cookie_based_affinity               = "Disabled"
      host_name                           = "amt.v1.fieldsmarttest.com"
      id                                  = ""
      name                                = "qa-https-settings"
      path                                = "/qa/"
      pick_host_name_from_backend_address = false
      port                                = 443
      probe_id                            = ""
      probe_name                          = "qa-probe"
      protocol                            = "HTTPS"
      request_timeout                     = 20
    },
    {
      cookie_based_affinity               = "Disabled"
      host_name                           = "amt.v1.fieldsmarttest.com"
      id                                  = ""
      name                                = "uat-https-settings"
      path                                = "/uat/"
      pick_host_name_from_backend_address = false
      port                                = 443
      probe_id                            = ""
      probe_name                          = "uat-probe"
      protocol                            = "HTTPS"
      request_timeout                     = 20
    },
    {
      cookie_based_affinity               = "Disabled"
      host_name                           = "amt.v1.fieldsmarttest.com"
      id                                  = ""
      name                                = "aut-https-settings"
      path                                = "/aut/"
      pick_host_name_from_backend_address = false
      port                                = 443
      probe_id                            = ""
      probe_name                          = "aut-probe"
      protocol                            = "HTTPS"
      request_timeout                     = 20
    }
  ]

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
  default = [{

    frontend_ip_configuration_name = "pip-hub-dev-uksouth-public" //pip-hub-dev-uksouth-enterprise
    frontend_port_name             = "HttpsPort"
    host_name                      = ""
    #host_names                    = ""
    #id                            = ""
    name                           = "https-listener"
    protocol                       = "HTTPS"
    require_sni                    = false
    ssl_certificate_id             = ""
    ssl_certificate_name           = "v1.fieldsmarttest.com"
  }
  ]
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
  default = [{

    frontend_ip_configuration_name = "pip-hub-dev-ukwest-public"  //pip-hub-dev-ukwest-enterprise
    frontend_port_name             = "HttpsPort"
    host_name                      = ""
    #host_names                    = ""
    #id                            = ""
    name                           = "https-listener"
    protocol                       = "HTTPS"
    require_sni                    = false
    ssl_certificate_id             = ""
    ssl_certificate_name           = "v1.fieldsmarttest.com"
  }
  ]
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
  default = [{
    data                = "./v1.fieldsmarttest.com.pfx"
    name                = "v1.fieldsmarttest.com"
    password            = "sxqltdemstl7@LP"

  }
  ]
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
  default = [{
    host                                      = ""
    interval                                  = 30
    name                                      = "dev-probe"
    path                                      = "/dev/mobile/actuator/health"
    pick_host_name_from_backend_http_settings = true
    port                                      = 443
    protocol                                  = "HTTPS"
    timeout                                   = 30
    unhealthy_threshold                       = 3
  },
    {
      host                                      = ""
      interval                                  = 30
      name                                      = "qa-probe"
      path                                      = "/qa/mobile/actuator/health"
      pick_host_name_from_backend_http_settings = true
      port                                      = 443
      protocol                                  = "HTTPS"
      timeout                                   = 30
      unhealthy_threshold                       = 3
    },
    {
      host                                      = ""
      interval                                  = 30
      name                                      = "uat-probe"
      path                                      = "/uat/mobile/actuator/health"
      pick_host_name_from_backend_http_settings = true
      port                                      = 443
      protocol                                  = "HTTPS"
      timeout                                   = 30
      unhealthy_threshold                       = 3
    },
    {
      host                                      = ""
      interval                                  = 30
      name                                      = "aut-probe"
      path                                      = "/aut/mobile/actuator/health"
      pick_host_name_from_backend_http_settings = true
      port                                      = 443
      protocol                                  = "HTTPS"
      timeout                                   = 30
      unhealthy_threshold                       = 3
    }
  ]
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
  default = [{
    backend_address_pool_name   = "dev"
    backend_http_settings_name  = "dev-https-settings"
    http_listener_name          = "https-listener"
    name                        = "mainrule"
    rule_type                   = "PathBasedRouting"
    url_path_map_name           = "mainrule"
  }]
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
  default = [
    {
      default_backend_address_pool_name   = "dev"
      default_backend_http_settings_name  = "dev-https-settings"
      name                                = "mainrule"
      path_rule = [
        {
          backend_address_pool_name  = "dev"
          backend_http_settings_name = "dev-https-settings"
          name                       = "dev"
          paths                      = ["/dev/*"]
        },
        {
          backend_address_pool_name  = "qa"
          backend_http_settings_name = "qa-https-settings"
          name                       = "qa"
          paths                      = ["/qa/*"]
        },
        {
          backend_address_pool_name  = "uat"
          backend_http_settings_name = "uat-https-settings"
          name                       = "uat"
          paths                      = ["/uat/*"]
        },
        {
          backend_address_pool_name  = "aut"
          backend_http_settings_name = "aut-https-settings"
          name                       = "aut"
          paths                      = ["/aut/*"]
        }

      ]
    }]
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
  default = [{

    enabled                    = true
    firewall_mode              = "Detection"
    rule_set_type              = "OWASP"
    rule_set_version           = "3.2"

    disabled_rule_group =[{
      rule_group_name = "REQUEST-933-APPLICATION-ATTACK-PHP"
      rules           = [933210,933100,933150]
    },
      {
        rule_group_name = "REQUEST-941-APPLICATION-ATTACK-XSS"
        rules = [941120,941100]
      },
      {
        rule_group_name = "REQUEST-942-APPLICATION-ATTACK-SQLI"
        rules = [ 942340,942450,942150,942410,942430]
      },
      {
        rule_group_name = "REQUEST-913-SCANNER-DETECTION"
        rules = [913100]
      },
      {
        rule_group_name = "REQUEST-920-PROTOCOL-ENFORCEMENT"
        rules = [920350,920320,920170,920280,920340,920341,920420]
      }

    ]
  }]
}


####################################### Traffic manager End point ##################

variable "type" {
  description = "(required)"
  type        = string
  default = "azureEndpoints"
}
 ###########################################Traffic manager profile ##########################

variable "profile_status" {
  description = "(optional)"
  type        = string
  default     = null
}

variable "tags" {
  description = "(optional)"
  type        = map(string)
  default     = null
}

variable "traffic_routing_method" {
  type        = string
  default     = "Performance"
}

variable "traffic_view_enabled" {
  type        = bool
  default     = false
}

variable "dns_config" {
  description = "nested block: NestingList, min items: 1, max items: 1"
  type = list(object(
  {
    relative_name = string
    ttl           = number
  }
  ))
  default = [{
    relative_name = ""
    ttl           = 60
  }]
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
  default = [{
    expected_status_code_ranges  = []
    path                         = "/dev/mobile/actuator/health"
    port                         = 443
    protocol                     = "https"
    timeout_in_seconds           = 10
    tolerated_number_of_failures = 3

  }
  ]
}

variable "log_analytics_workspace_name" {
  type        = string
  default     = "log-plat-dev-shared"
}

variable "platform_shared_rg_name" {
  type    = string
  default = "rg-plat-dev-shared"
}

variable "appgateway_audit_diagnostics_logs" {
  type    = list(string)
  default = ["ApplicationGatewayAccessLog","ApplicationGatewayPerformanceLog","ApplicationGatewayFirewallLog"]
}
variable "appgateway_audit_diagnostics_metrics" {
  type    = list(string)
  default = ["AllMetrics"]
}

variable "traffic_manager_audit_diagnostics_logs" {
  type    = list(string)
  default = ["ProbeHealthStatusEvents"]
}
variable "traffic_manager_diagnostics_metrics" {
  type    = list(string)
  default = ["AllMetrics"]
}

variable "ipgroup_cidrs" {
  description = "(optional)"
  type        = list(set(string))
  default     = [["172.16.1.0/26", "10.16.1.0/26"]]
}

variable "ipgroup_names" {
  description = "(required)"
  type        = list(string)
  default     = ["ipgroup-hub-lower-ag"]
}

variable "fwp_resource_group_name" {
  type    = string
  default = "rg-hub-dev-shared"
}