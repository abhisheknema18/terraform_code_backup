# identifiers
variable "env" {
  type    = string

}

variable "region" {
  type    = string

}

variable "region_sec" {
  type    = string

}

variable "region_shared" {
  type    = string

}

variable "isMultiregion" {
  type    = bool

}

variable "app" {
  type    = string

}

variable "app_global" {
  type    = string

}

variable "log_analytics_workspace_name" {
  type        = string

}

variable "aks_client_id" {
  type    = string
  }

variable "aks_client_secret" {
  type    = string
  }

variable "aks_kubernetes_version" {
  type    = string

}

variable "aks_orchestrator_version" {
  type    = string

}

variable "aks_network_plugin" {
  type = string
}

variable "aks_os_disk_size_gb" {
  type    = number

}

variable "aks_sku_tier" {
  type    = string

}

variable "aks_private_cluster_enabled" {
  type    = bool

}

variable "aks_enable_azure_policy" {
  type    = bool

}

variable "aks_enable_auto_scaling" {
  type    = bool

}

variable "aks_enable_host_encryption" {
  type    = bool
  }

variable "aks_agents_min_count" {
  type    = number

}

variable "aks_agents_max_count" {
  type    = number

}

variable "aks_agents_count" {
  type    = number

}


variable "aks_agents_max_pods" {
  type    = number
  }


variable "aks_agents_pool_name" {
  type    = string

}


variable "aks_agents_type" {
  type    = string

}

variable "aks_enable_rbac" {
  type    = bool

}

variable "aks_agents_availability_zones" {
  type    = list(string)


}

variable "aks_agents_labels_nodepool" {
  type    = string

}

variable "aks_agents_tags_Agent" {
  type    = string

}

variable "aks_network_policy" {
  type    = string

}

variable "aks_net_profile_dns_service_ip" {
  type    = string

}

variable "aks_net_profile_dns_service_ip_ukwest" {
  type    = string

}

variable "aks_net_profile_docker_bridge_cidr" {
  type    = string

}

variable "aks_net_profile_service_cidr" {
  type    = string
}

variable "aks_net_profile_service_cidr_ukwest" {
  type    = string
}

variable "network_address_spaces" {
  type    = list(string)
}

variable "network_address_spaces_ukwest" {
  type    = list(string)
}

variable "network_subnet_prefixes" {
  type    = list(string)
}

variable "network_subnet_prefixes_ukwest" {
  type    = list(string)
}

variable "network_subnet_names" {
  type    = list(string)
}
variable "subnet_service_endpoints" {
  type    = map(list(string))
 
}

variable "network_global_address_spaces" {
  type    = list(string)
}

variable "network_global_subnet_prefixes" {
  type    = list(string)
}

variable "network_global_subnet_names" {
  type    = list(string)
}

variable "network_subnet_enforce_private_link_endpoint_network_policies" {
  type    = map(bool)
  
}

variable "nsg_predefined_rules" {
  type    = any
  
}

variable "routetable_disable_bgp_route_propagation" {
  type    = bool
}

variable "routetable_enable_force_tunneling" {
  type    = bool
}

variable "acr_name" {
  type    = string
}

variable "platform_shared_rg_name" {
  type    = string
}

variable "tag_behavior" {
  type    = string
}

variable "tag_nb_resources" {
  type    = number
}

variable "sa_file_share_audit_diagnostics_logs" {
  type    = list(string)
  
}

variable "sa_file_share_audit_diagnostics_metrics" {
  type    = list(string)
  
}

variable "kv_audit_diagnostics_logs" {
  type    = list(string)
 
}

variable "kv_audit_diagnostics_metrics" {
  type    = list(string)
 
}

variable "aks_audit_diagnostics_logs" {
  type    = list(string)
  
}

variable "aks_audit_diagnostics_metrics" {
  type    = list(string)
 
}
variable "public_ssh_key" {
  type    = string
}

variable "ssh_private_key_filename" {
  type = string
}

variable "ssh_public_key_filename" {
  type = string
}

variable "ssh_integration_service_public_key_filename" {
  type = string
}

variable "ssh_integration_service_private_key_filename" {
  type = string
}

variable "ssh_integration_service_pkcs8_private_key_filename" {
  type = string
}

variable "vm_is_windows_image" {
  type    = bool
}


variable "windows_nb_instances" {
  type = number
}

variable "vm_admin_password" {
  type    = string
}

variable "vm_os_simple" {
  type    = string
}

variable "vm_number_of_public_ip" {
  type    = number
}

variable "kv_sku" {
  type    = string
}

variable "kv_soft_delete_retention_days" {
  type    = number
}

variable "kv_tags" {
  type = map(string)
  
}

variable "kv_secrets_map" {
  type = map(string)
}

variable "kv_secret_private_key" {
  type    = string
}

variable "kv_secret_public_key" {
  type    = string
}

variable "kv_key_permissions_read" {
  type        = list(string) 
}

variable "kv_secret_permissions_read" {
  type        = list(string)
}

variable "kv_certificate_permissions_read" {
  type        = list(string)
}

variable "kv_storage_permissions_no_access" {
  type = list(string)
  
}

variable "fs_name" {
  type    = string
}

variable "fs_quota" {
  type = number
}

variable "fs_primary_directories" {
  
  type        = list(string)

}

variable "fs_secondary_directories" {
  type        = list(string)
 
  }

variable "hub_vnet_name" {
  type    = string
}

variable "hub_vnet_resource_group" {
  type    = string
}

variable "routetable_next_hop_type" {
  type    = string
}

variable "routetable_next_hop_in_ip_address" {
  type    = string
}

variable "tenant_primary_sqlserver_name" {
  type    = string
}

variable "tenant_sqlserver_resource_group" {
  type    = string
}

variable "azurerm_private_dns_zone_sql_name" {
  type    = string
}

variable "azurerm_private_dns_zone_acr_name" {
  type    = string
}


variable "platform_operationvm_subnet_name" {
  type    = string
}

variable "platform_adovm_subnet_name" {
  type    = string
}

variable "platform_operationvm_vnet_name" {
  type    = string
}

variable "IaaSAntimalware_type_handler_version" {
  type    = string
}

variable "delete_os_disk_on_termination" {
  type        = bool
}
