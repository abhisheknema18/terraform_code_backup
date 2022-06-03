variable "location" {
  type = string
}

variable "resource_group_name" {
  type = string
}

variable "name" {
  type    = string
  default = ""
}

variable "disable_bgp_route_propagation" {
  type    = bool
  default = false
}

variable "enable_force_tunneling" {
  type    = bool
  default = false
}

variable "next_hop_type" {
  type    = string
  default = "VirtualNetworkGateway"
}

variable "route_address_prefix" {
  type    = string
  default = "0.0.0.0/0"
}

variable "next_hop_in_ip_address" {
  type    = string
  default = ""
}
