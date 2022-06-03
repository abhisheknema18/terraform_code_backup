output "firewall_public_ip" {
  value = module.firewall.public_ips[0].ip_address
}

output "firewall_private_ip" {
  value = module.firewall.firewall_private_ip
}