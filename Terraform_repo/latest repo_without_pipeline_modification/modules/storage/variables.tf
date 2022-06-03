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

variable "cors_rule" {
  type = list(object({
    allowed_origins    = list(string)
    allowed_methods    = list(string)
    allowed_headers    = list(string)
    exposed_headers    = list(string)
    max_age_in_seconds = number
  }))
  default = []
}

variable "enable_advanced_threat_protection" {
  type        = bool
  default     = false
}

variable "network_rules" {
  type = object({
    ip_rules   = list(string)
    subnet_ids = list(string)
    bypass     = list(string)
  })
  default = null
}

variable "diagnostics" {
  type = object({
    destination   = string
    logs          = list(string)
    metrics       = list(string)
  })
  default = null
}