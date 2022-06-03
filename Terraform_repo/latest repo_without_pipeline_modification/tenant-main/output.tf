output "platform" {
  value = tolist([data.azurerm_subnet.platform.id])
}

output "vnet_subnets" {
  value =  module.network.vnet_subnets
}

output "sb_vm_private_ips" {
  value =  module.windowsservers[0].network_interface_private_ip
}

