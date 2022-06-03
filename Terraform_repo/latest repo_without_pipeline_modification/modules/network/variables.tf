variable "vnet_name" {
  type    = string
  default = "acctvnet"
}

variable "resource_group_name" {
  type = string
}

variable "location" {
  type = string
}

variable "address_space" {
  type    = string
  default = "10.0.0.0/16"
}

variable "address_spaces" {
  type    = list(string)
  default = []
}

variable "dns_servers" {
  type    = list(string)
  default = []
}

variable "subnet_prefixes" {
  type    = list(string)
  default = ["10.0.1.0/24"]
}

variable "subnet_names" {
  type    = list(string)
  default = ["subnet1"]
}

variable "tags" {
  type = map(string)

  default = {
    environment = "dev"
  }
}

variable "subnet_enforce_private_link_endpoint_network_policies" {
  type    = map(bool)
  default = {}
}

variable "subnet_service_endpoints" {
  type    = map(list(string))
  default = {}
}
