variable "resource_group_name" {
  type        = string
}

variable "location" {
  type        = string
  default     = ""
}

variable "vnet_subnet_id" {
  type        = string
}

variable "public_ip_dns" {
  type        = list(string)
  default     = [null]
}

variable "admin_password" {
  type        = string
  default     = ""
}

variable "remote_port" {
  type        = string
  default     = ""
}

variable "admin_username" {
  type        = string
  default     = "azureuser"
}

variable "custom_data" {
  type        = string
  default     = ""
}

variable "storage_account_type" {
  type        = string
  default     = "Premium_LRS"
}

variable "vm_size" {
  type        = string
  default     = "Standard_D2s_v3"
}

variable "nb_instances" {
  type        = number
  default     = 1
}

variable "vm_hostname" {
  type        = string
  default     = "myvm"
}

variable "vm_os_simple" {
  type        = string
  default     = ""
}

variable "vm_os_id" {
  type        = string
  default     = ""
}



variable "vm_os_publisher" {
  type        = string
  default     = ""
}

variable "vm_os_offer" {
  #type        = string
  default     = ""
}

variable "vm_os_sku" {
  type        = string
  default     = ""
}

variable "vm_os_version" {
  type        = string
  default     = "latest"
}

variable "tags" {
  type        = map(string)

  default = {
    source = "terraform"
  }
}

variable "allocation_method" {
  type        = string
  default     = "Dynamic"
}

variable "public_ip_sku" {
  type        = string
  default     = "Basic"
}

variable "nb_public_ip" {
  type        = number
  default     = 0
}

variable "delete_os_disk_on_termination" {
  type        = bool
  default     = false
}

variable "delete_data_disks_on_termination" {
  type        = bool
  default     = false
}

variable "data_sa_type" {
  type        = string
  default     = "Standard_LRS"
}

variable "data_disk_size_gb" {
  type        = number
  default     = 30
}

variable "boot_diagnostics" {
  type        = bool
  default     = false
}

variable "boot_diagnostics_sa_type" {
  type        = string
  default     = "Standard_LRS"
}

variable "enable_accelerated_networking" {
  type        = bool
  default     = false
}

variable "enable_ssh_key" {
  type        = bool
  default     = true
}

variable "nb_data_disk" {
  type        = number
  default     = 0
}


variable "license_type" {
  type        = string
  default     = null
}

variable "identity_type" {
  type        = string
  default     = ""
}

variable "identity_ids" {
  type        = list(string)
  default     = []
}

variable "extra_disks" {
  type = list(object({
    name = string
    size = number
  }))
  default = []
}

variable "os_profile_secrets" {
  type        = list(map(string))
  default     = []
}

variable "data_disk" {
  type = bool
  default = false
}

variable "os_flavor" {
  type = string
  default = ""
}


variable "availability_set_id" {
  type = string
  default = ""
}


variable "diagnostics" {
  type = object({
    destination   = string
    logs          = list(string)
    metrics       = list(string)
  })
  default = null
}

variable "type_handler_version" {
  type    = string
  default = ""
}
