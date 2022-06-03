variable "name" {
}

variable "resource_group_name" {
}

variable "location" {
}

variable "account_tier" {
  default     = "Standard"
}

variable "account_replication_type" {
  default     = "ZRS"
}

variable "access_tier" {
  default     = "Hot"
}

variable "min_tls_version" {
  default     = "TLS1_2"
}

variable "soft_delete_retention" {
  type        = number
  default     = 31
}

variable "enable_advanced_threat_protection" {
  type        = bool
  default     = false
}

variable "network_rules" {
  type = set(object(
  {
    bypass                     = list(string)
    default_action             = string
    ip_rules                   = set(string)
    virtual_network_subnet_ids = list(string)
  }))
  default = []
}

variable "diagnostics" {
  type = object({
    destination   = string
    logs          = list(string)
    metrics       = list(string)
  })
  default = null
}

variable "file_share_name" {
  description = "(required)"
  type        = string
}

variable "quota" {
  description = "(optional)"
  type        = number
  default     = null
}

variable "primary_metadata" {
  description = "(optional)"
  type        = map(string)
  default     = null
}

variable "secondary_metadata" {
  description = "(optional)"
  type        = map(string)
  default     = null
}

variable "primary_directories" {
  description = "(optional)"
  type        = list(string)
  default = []
}

variable "secondary_directories" {
  description = "(optional)"
  type        = list(string)
  default = []
}