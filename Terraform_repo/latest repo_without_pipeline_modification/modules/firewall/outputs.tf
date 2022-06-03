output "id" {
  description = "returns a string"
  value       = azurerm_firewall.this.id
}

output "sku_name" {
  description = "returns a string"
  value       = azurerm_firewall.this.sku_name
}

output "sku_tier" {
  description = "returns a string"
  value       = azurerm_firewall.this.sku_tier
}

output "this" {
  value = azurerm_firewall.this
}

output "public_ips" {
  value       = azurerm_public_ip.this.*
}

output "public_ip_id" {
  value       = azurerm_public_ip.this.*.id
}

output "public_ip_address" {
  value       = azurerm_public_ip.this.*.ip_address
}

output "public_ip_dns_name" {
  value       = azurerm_public_ip.this.*.fqdn
}

output "firewall_policy_id" {
  description = "returns a string"
  value       = azurerm_firewall_policy.this.id
}

output "sku" {
  description = "returns a string"
  value       = azurerm_firewall_policy.this.sku
}

output "firewall_policy" {
  value = azurerm_firewall_policy.this
}

output "firewall_private_ip" {
  value = length(azurerm_firewall.this.ip_configuration) > 0 ? tolist(azurerm_firewall.this.ip_configuration)[0].private_ip_address : ""
}
