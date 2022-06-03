output "window_vm_ids" {
  value       = concat(azurerm_virtual_machine.windowvm.*.id)
}

output "network_interface_ids" {
  value       = azurerm_network_interface.this.*.id
}

output "network_interface_private_ip" {
  value       = azurerm_network_interface.this.*.private_ip_address
}

output "public_ip_id" {
  value       = azurerm_public_ip.this.*.id
}

output "public_ip_dns_name" {
  value       = azurerm_public_ip.this.*.fqdn
}

