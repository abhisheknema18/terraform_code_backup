output "id" {
  value       = azurerm_ip_group.this.*.id
}

output "this" {
  value = azurerm_ip_group.this.*
}