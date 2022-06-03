variable "cidrs" {
  description = "(optional)"
  type        = list(set(string))
  default     = null
}

variable "location" {
  description = "(required)"
  type        = string
}

variable "names" {
  description = "(required)"
  type        = list(string)
}

variable "resource_group_name" {
  description = "(required)"
  type        = string
}

variable "tags" {
  description = "(optional)"
  type        = map(string)
  default     = null
}

variable "timeouts" {
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
