#resource "azurerm_availability_set" "this" {
#  name                         = "${var.vm_hostname}-avset"
#  resource_group_name          = var.resource_group_name
#  location                     = var.location
#  platform_fault_domain_count  = 2
#  platform_update_domain_count = 2
#  managed                      = true
#  tags                         = var.tags
#  lifecycle {
#    ignore_changes = [
#      tags,
#    ]
#  }
#}


resource "azurerm_availability_set" "this" {
  name = var.availability_set_name
  resource_group_name = var.resource_group_name
  location = var.location
  managed = var.managed
  platform_fault_domain_count = var.platform_fault_domain_count
  platform_update_domain_count = var.platform_update_domain_count
  tags = var.tags
  lifecycle {
    ignore_changes = [
      tags,
    ]
  }


}