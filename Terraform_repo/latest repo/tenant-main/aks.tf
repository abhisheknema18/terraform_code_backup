module "aks" {
  # source                           = var.mod_env == "local" ? "../modules/aks/" : ""
  source                           = "../modules/aks/"
  resource_group_name              = azurerm_resource_group.this.name
  client_id                        = var.aks_client_id
  client_secret                    = var.aks_client_secret
  kubernetes_version               = var.aks_kubernetes_version
  orchestrator_version             = var.aks_orchestrator_version
  cluster_name                     = module.naming.kubernetes_cluster.name
  network_plugin                   = var.aks_network_plugin
  vnet_subnet_id                   = module.network.vnet_subnets[0]
  os_disk_size_gb                  = var.aks_os_disk_size_gb
  sku_tier                         = var.aks_sku_tier
  public_ssh_key                   = module.ssh_key.public_ssh_key
  private_cluster_enabled          = var.aks_private_cluster_enabled  // change to true as soon as we have private infra in place
  enable_azure_policy              = var.aks_enable_azure_policy // change to true soon RBAC and other azure policies are enabled.
  enable_auto_scaling              = var.aks_enable_auto_scaling
  enable_host_encryption           = var.aks_enable_host_encryption
  agents_min_count                 = var.aks_agents_min_count
  agents_max_count                 = var.aks_agents_max_count
  agents_count                     = var.aks_agents_count
  agents_max_pods                  = var.aks_agents_max_pods
  agents_pool_name                 = var.aks_agents_pool_name
  agents_availability_zones        = var.aks_agents_availability_zones
  agents_type                      = var.aks_agents_type
  enable_role_based_access_control = var.aks_enable_rbac

  log_analytics_workspace_id       = data.azurerm_log_analytics_workspace.platform.id
  # acr_id = data.azurerm_container_registry.platform.id
  agents_labels = {
    "nodepool" : var.aks_agents_labels_nodepool
  }

  agents_tags = {
    "Agent" : var.aks_agents_tags_Agent
  }

  network_policy                    = var.aks_network_policy
  net_profile_dns_service_ip        = var.aks_net_profile_dns_service_ip
  net_profile_docker_bridge_cidr    = var.aks_net_profile_docker_bridge_cidr
  net_profile_service_cidr          = var.aks_net_profile_service_cidr
  diagnostics = {
    destination     = data.azurerm_log_analytics_workspace.platform.id
    logs            = var.aks_audit_diagnostics_logs
    metrics         = var.aks_audit_diagnostics_metrics
  }
  depends_on = [module.network, azurerm_subnet_route_table_association.sn_rt_association_aks_firewall]
}
