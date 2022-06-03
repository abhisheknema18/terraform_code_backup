resource "azurerm_traffic_manager_endpoint" "this" {
  name = var.name
  profile_name = var.profile_name
  resource_group_name = var.resource_group_name
  target_resource_id = var.target_resource_id
  type = var.type

}