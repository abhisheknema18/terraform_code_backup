module "os" {
  source       = "./../os"
  vm_os_simple = var.vm_os_simple
}

  resource "azurerm_virtual_machine" "windowvm" {
    #count                         = var.nb_instances == "" ? 0 : var.nb_instances
    #name                          = var.nb_instances == 1 ? substr(var.vm_hostname, 0, 64) : substr(format("%s%s", lower(replace(var.vm_hostname, "/[[:^alnum:]]/", "")), count.index + 1), 0, 64)
    name                           = var.vm_hostname
    resource_group_name            = var.resource_group_name
    location                      = var.location
    #availability_set_id           = length(azurerm_availability_set.this) > 0 ? azurerm_availability_set.this[0].id : null
    availability_set_id            = var.availability_set_id
    vm_size                       = var.vm_size
    network_interface_ids         = azurerm_network_interface.this.*.id
    #network_interface_ids         = [element(azurerm_network_interface.this.*.id, count.index)]
    delete_os_disk_on_termination = var.delete_os_disk_on_termination
    license_type                  = var.license_type

    dynamic identity {
      for_each = length(var.identity_ids) == 0 && var.identity_type == "SystemAssigned" ? [var.identity_type] : []
      content {
        type = var.identity_type
      }
    }

    dynamic identity {
      for_each = length(var.identity_ids) > 0 || var.identity_type == "UserAssigned" ? [var.identity_type] : []
      content {
        type         = var.identity_type
        identity_ids = length(var.identity_ids) > 0 ? var.identity_ids : []
      }
    }

    storage_image_reference {
      id        = var.vm_os_id
      publisher = var.vm_os_id == "" ? coalesce(var.vm_os_publisher, module.os.calculated_value_os_publisher) : ""
      offer     = var.vm_os_id == "" ? coalesce(var.vm_os_offer, module.os.calculated_value_os_offer) : ""
      sku       = var.vm_os_id == "" ? coalesce(var.vm_os_sku, module.os.calculated_value_os_sku) : ""
      version   = var.vm_os_id == "" ? var.vm_os_version : ""
    }

    storage_os_disk {
      name              = "${var.vm_hostname}-osdisk"
      create_option     = "FromImage"
      caching           = "ReadWrite"
      managed_disk_type = var.storage_account_type
    }

    dynamic storage_data_disk {
      for_each = range(var.nb_data_disk)
      content {
        name              = "${var.vm_hostname}-datadisk-${storage_data_disk.value}"
        create_option     = "Empty"
        lun               = storage_data_disk.value
        disk_size_gb      = var.data_disk_size_gb
        managed_disk_type = var.data_sa_type
      }
    }

    dynamic storage_data_disk {
      for_each = var.extra_disks
      content {
        name              = "${var.vm_hostname}-extradisk-${storage_data_disk.value.name}"
        create_option     = "Empty"
        lun               = storage_data_disk.key + var.nb_data_disk
        disk_size_gb      = storage_data_disk.value.size
        managed_disk_type = var.data_sa_type
      }
    }

    os_profile {
      computer_name  = "${var.vm_hostname}"
      admin_username = var.admin_username
      admin_password = var.admin_password
    }

    tags = var.tags

    os_profile_windows_config {
      provision_vm_agent = true
    }

    dynamic "os_profile_secrets" {
      for_each = var.os_profile_secrets
      content {
        source_vault_id = os_profile_secrets.value["source_vault_id"]

        vault_certificates {
          certificate_url   = os_profile_secrets.value["certificate_url"]
          certificate_store = os_profile_secrets.value["certificate_store"]
        }
      }
    }

  }

#
#  resource "azurerm_availability_set" "this" {
#    count                        = var.nb_instances >= 2 ? 1 : 0
#    name                         = "${var.vm_hostname}-avset-${count.index}"
#    resource_group_name          = var.resource_group_name
#    location                     = var.location
#    platform_fault_domain_count  = 2
#    platform_update_domain_count = 2
#    managed                      = true
#    tags                         = var.tags
#    lifecycle {
#      ignore_changes = [
#        tags,
#      ]
#    }
#  }

  resource "azurerm_public_ip" "this" {
    count               = var.nb_public_ip
    name                = "${var.vm_hostname}-pip-${count.index}"
    resource_group_name = var.resource_group_name
    location            = var.location
    allocation_method   = var.allocation_method
    sku                 = var.public_ip_sku
    domain_name_label   = element(var.public_ip_dns, count.index)
  }


  resource "azurerm_network_interface" "this" {
    #count                         = var.nb_instances
    name                          = "${var.vm_hostname}-nic"
    resource_group_name           = var.resource_group_name
    location                      = var.location
    enable_accelerated_networking = var.enable_accelerated_networking

    ip_configuration {
      name                          = "${var.vm_hostname}-ip"
      subnet_id                     = var.vnet_subnet_id
      private_ip_address_allocation = "Dynamic"
      #public_ip_address_id          = length(azurerm_public_ip.this.*.id) > 0 ? element(concat(azurerm_public_ip.this.*.id, tolist([""])), count.index) : ""
    }
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
  name                           = "${var.vm_hostname}-win-vm-diag"
  target_resource_id             = azurerm_virtual_machine.windowvm.id
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
}

resource "azurerm_virtual_machine_extension" "this" {
  count                = var.nb_instances
  name                 = "IaaSAntimalware"
  virtual_machine_id   = azurerm_virtual_machine.windowvm.id
  publisher            = "Microsoft.Azure.Security"
  type                 = "IaaSAntimalware"
  type_handler_version = var.type_handler_version
  auto_upgrade_minor_version = true
 }