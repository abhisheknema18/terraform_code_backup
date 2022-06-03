# Network Security Group definition
variable "resource_group_name" {
  type = string
}

variable "security_group_name" {
  type    = string
  default = "nsg"
}

variable "tags" {
  type    = map(string)
  default = {}
}

variable "location" {
  type    = string
  default = ""
}

variable "predefined_rules" {
  type    = any
  default = []
}

variable "custom_rules" {
  description = "Security rules for the network security group using this format name = [priority, direction, access, protocol, source_port_range, destination_port_range, source_address_prefix, destination_address_prefix, description]"
  type        = any
  default     = []
}

variable "source_address_prefix" {
  type    = list(string)
  default = ["*"]
}

variable "source_address_prefixes" {
  type    = list(string)
  default = null
}

variable "destination_address_prefix" {
  type    = list(string)
  default = ["*"]
}

variable "destination_address_prefixes" {
  type    = list(string)
  default = null
}
