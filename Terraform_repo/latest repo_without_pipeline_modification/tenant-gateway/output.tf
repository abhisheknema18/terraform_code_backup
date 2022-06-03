output "azure_public_ip" {
  value = azurerm_public_ip.this.ip_address
}

output "dns_config" {
  value = toset(var.dns_config[*].ttl)
}