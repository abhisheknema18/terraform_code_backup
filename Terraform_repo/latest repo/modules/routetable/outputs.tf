output "route_table_name" {
  description = "Route table name"
  value       = azurerm_route_table.this.name
}

output "route_table_id" {
  description = "Route table ID"
  value       = azurerm_route_table.this.id
}

output "route_force_tunneling" {
  description = "Force tunneling route status"
  value       = var.enable_force_tunneling
}

