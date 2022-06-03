data "azurerm_resource_group" "main" {
  name = var.resource_group_name
}

resource "azurerm_kubernetes_cluster" "main" {
  name                    = var.cluster_name
  dns_prefix              = var.dns_prefix
  kubernetes_version      = var.kubernetes_version
  location                = data.azurerm_resource_group.main.location
  resource_group_name     = data.azurerm_resource_group.main.name
  sku_tier                = var.sku_tier
  private_cluster_enabled = var.private_cluster_enabled

  linux_profile {
    admin_username = var.admin_username

    ssh_key {
      key_data = replace(var.public_ssh_key, "\n", "")
    }
  }

  dynamic "default_node_pool" {
    for_each = var.enable_auto_scaling == true ? [] : ["default_node_pool_manually_scaled"]
    content {
      orchestrator_version   = var.orchestrator_version
      name                   = var.agents_pool_name
      node_count             = var.agents_count
      vm_size                = var.agents_size
      os_disk_size_gb        = var.os_disk_size_gb
      vnet_subnet_id         = var.vnet_subnet_id
      enable_auto_scaling    = var.enable_auto_scaling
      max_count              = null
      min_count              = null
      enable_node_public_ip  = var.enable_node_public_ip
      availability_zones     = var.agents_availability_zones
      node_labels            = var.agents_labels
      type                   = var.agents_type
      max_pods               = var.agents_max_pods
      enable_host_encryption = var.enable_host_encryption
    }
  }

  dynamic "default_node_pool" {
    for_each = var.enable_auto_scaling == true ? ["default_node_pool_auto_scaled"] : []
    content {
      orchestrator_version   = var.orchestrator_version
      name                   = var.agents_pool_name
      vm_size                = var.agents_size
      os_disk_size_gb        = var.os_disk_size_gb
      vnet_subnet_id         = var.vnet_subnet_id
      enable_auto_scaling    = var.enable_auto_scaling
      max_count              = var.agents_max_count
      min_count              = var.agents_min_count
      enable_node_public_ip  = var.enable_node_public_ip
      availability_zones     = var.agents_availability_zones
      node_labels            = var.agents_labels
      type                   = var.agents_type
      max_pods               = var.agents_max_pods
      enable_host_encryption = var.enable_host_encryption
    }
  }

  dynamic "service_principal" {
    for_each = var.client_id != "" && var.client_secret != "" ? ["service_principal"] : []
    content {
      client_id     = var.client_id
      client_secret = var.client_secret
    }
  }

  dynamic "identity" {
    for_each = var.client_id == "" || var.client_secret == "" ? ["identity"] : []
    content {
      type                      = var.identity_type
      user_assigned_identity_id = var.user_assigned_identity_id
    }
  }

  addon_profile {
    http_application_routing {
      enabled = false
    }

    kube_dashboard {
      enabled = var.enable_kube_dashboard
    }

    azure_policy {
      enabled = var.enable_azure_policy
    }

    oms_agent {
      enabled                    = true
      log_analytics_workspace_id = var.log_analytics_workspace_id
    }
  }

  role_based_access_control {
    enabled = var.enable_role_based_access_control
  }

  network_profile {
    network_plugin     = var.network_plugin
    network_policy     = var.network_policy
    dns_service_ip     = var.net_profile_dns_service_ip
    docker_bridge_cidr = var.net_profile_docker_bridge_cidr
    outbound_type      = var.net_profile_outbound_type
    pod_cidr           = var.net_profile_pod_cidr
    service_cidr       = var.net_profile_service_cidr
  }
}

resource "azurerm_role_assignment" "acr" {
  count = var.acr_id == null ? 0 : 1

  scope                = var.acr_id
  role_definition_name = "AcrPull"
  principal_id         = var.client_id
}




locals {
  diag_resource_list = var.diagnostics != null ? split("/", var.diagnostics.destination) : []
  parsed_diag = var.diagnostics != null ? {
    log_analytics_id   = contains(local.diag_resource_list, "Microsoft.OperationalInsights") ? var.diagnostics.destination : null
    metric             = var.diagnostics.metrics
    log                = var.diagnostics.logs
  } : {
    log_analytics_id   = null
    metric             = []
    log                = []
  }
}
resource "azurerm_monitor_diagnostic_setting" "this" {
  count                          = var.diagnostics != null ? 1 : 0
  name                           = "${var.cluster_name}-aks-diag"
  target_resource_id             = azurerm_kubernetes_cluster.main.id
  log_analytics_workspace_id     = local.parsed_diag.log_analytics_id

  dynamic "log" {
    for_each = var.diagnostics.logs
    content {
      category = log.value
      enabled  = contains(local.parsed_diag.log, "all") || contains(local.parsed_diag.log, log.value)

      retention_policy {
        enabled = false
        days    = 0
      }
    }
  }

  dynamic "metric" {
    for_each = var.diagnostics.metrics
    content {
      category = metric.value
      enabled  = contains(local.parsed_diag.metric, "all") || contains(local.parsed_diag.metric, metric.value)

      retention_policy {
        enabled = false
        days    = 0
      }
    }
  }

  lifecycle {
    ignore_changes = [
      log,
      metric
    ]
  }
}
