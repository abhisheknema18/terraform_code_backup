variable "access_policy" {
  description = "(optional)"
  type = list(object(
  {
    application_id          = string
    certificate_permissions = list(string)
    key_permissions         = list(string)
    object_id               = string
    secret_permissions      = list(string)
    storage_permissions     = list(string)
    tenant_id               = string
  }
  ))
  default = null
}

variable "enable_rbac_authorization" {
  description = "(optional)"
  type        = bool
  default     = false
}

variable "enabled_for_deployment" {
  description = "(optional)"
  type        = bool
  default     = false
}

variable "enabled_for_disk_encryption" {
  description = "(optional)"
  type        = bool
  default     = false
}

variable "enabled_for_template_deployment" {
  description = "(optional)"
  type        = bool
  default     = false
}

variable "location" {
  description = "(required)"
  type        = string
}

variable "name" {
  description = "(required)"
  type        = string
}

variable "purge_protection_enabled" {
  description = "(optional)"
  type        = bool
  default     = null
}

variable "resource_group_name" {
  description = "(required)"
  type        = string
}

variable "sku_name" {
  description = "(required)"
  type        = string
}

variable "soft_delete_retention_days" {
  description = "(optional)"
  type        = number
  default     = 90
}

variable "tags" {
  description = "(optional)"
  type        = map(string)
  default     = null
}

variable "tenant_id" {
  description = "(required)"
  type        = string
}

variable "full_access_object_id" {
  description = "(required)"
  type        = string
}

variable "contact" {
  description = "nested block: NestingSet, min items: 0, max items: 0"
  type = set(object(
  {
    email = string
    name  = string
    phone = string
  }
  ))
  default = []
}

variable "network_acls" {
  description = "nested block: NestingList, min items: 0, max items: 1"
  type = set(object(
  {
    bypass                     = string
    default_action             = string
    ip_rules                   = set(string)
    virtual_network_subnet_ids = list(string)
  }
  ))
  default = []
}

variable "timeouts" {
  description = "nested block: NestingSingle, min items: 0, max items: 0"
  type = set(object(
  {
    create = string
    delete = string
    read   = string
    update = string
  }
  ))
  default = []
}

variable "secrets_map" {
  type = map(string)
  default = null
}

variable "policies" {
  type = map(object({
    tenant_id               = string
    object_id               = string
    key_permissions         = list(string)
    secret_permissions      = list(string)
    certificate_permissions = list(string)
    storage_permissions     = list(string)
  }))
  description = "Define a Azure Key Vault access policy"
  default = {}
}

variable "kv_key_permissions_full" {
  type        = list(string)
  description = "List of full key permissions, must be one or more from the following: backup, create, decrypt, delete, encrypt, get, import, list, purge, recover, restore, sign, unwrapKey, update, verify and wrapKey."
  default     = [ "backup", "create", "decrypt", "delete", "encrypt", "get", "import", "list", "purge",
    "recover", "restore", "sign", "unwrapKey","update", "verify", "wrapKey" ]
}

variable "kv_secret_permissions_full" {
  type        = list(string)
  description = "List of full secret permissions, must be one or more from the following: backup, delete, get, list, purge, recover, restore and set"
  default     = [ "backup", "delete", "get", "list", "purge", "recover", "restore", "set" ]
}

variable "kv_certificate_permissions_full" {
  type        = list(string)
  description = "List of full certificate permissions, must be one or more from the following: backup, create, delete, deleteissuers, get, getissuers, import, list, listissuers, managecontacts, manageissuers, purge, recover, restore, setissuers and update"
  default     = [ "create", "delete", "deleteissuers", "get", "getissuers", "import", "list", "listissuers",
    "managecontacts", "manageissuers", "purge", "recover", "setissuers", "update", "backup", "restore" ]
}

variable "kv_storage_permissions_full" {
  type        = list(string)
  description = "List of full storage permissions, must be one or more from the following: backup, delete, deletesas, get, getsas, list, listsas, purge, recover, regeneratekey, restore, set, setsas and update"
  default     = [ "backup", "delete", "deletesas", "get", "getsas", "list", "listsas",
    "purge", "recover", "regeneratekey", "restore", "set", "setsas", "update" ]
}

variable "diagnostics" {
  type = object({
    destination   = string
    logs          = list(string)
    metrics       = list(string)
  })
  default = null
}
