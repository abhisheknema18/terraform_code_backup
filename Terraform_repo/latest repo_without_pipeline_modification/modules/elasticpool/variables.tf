variable "sql_elasticpool_name" {
  type    = string
}
variable "sql_elasticpool_dtu" {
  type = number
}
variable "sql_elasticpool_edition" {
  type = string
}
variable "sql_elasticpool_db_dtu_min" {
  type = number
}
variable "sql_elasticpool_db_dtu_max" {
  type = number
}
variable "sql_elasticpool_max_size_gb" {
  type = number
}
variable "sql_elasticpool_sku_name" {
  type = string
}
variable "resource_group_name" {
  type = string
}
variable "location" {
  type = string
}
variable "sql_server_name" {
  type = string
}
variable "tags" {
  type = map(string)
  default = {
    environment = "dev"
  }
}

variable "diagnostics" {
  type = object({
    name          = string
    destination   = string
    logs          = list(string)
    metrics       = list(string)
  })
  default = null
}

