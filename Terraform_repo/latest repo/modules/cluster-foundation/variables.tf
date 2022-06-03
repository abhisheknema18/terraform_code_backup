variable "chart" {
  type        = string
  default = ""
}

variable "helm_version" {
  type        = string
  default     = ""
}
variable "create_namespace" {
  type        = bool
  default     = null
}

variable "name" {
  type        = string
  default = ""
}

variable "namespace" {

  type        = string
  default     = null
}

variable "repository" {
  type        = string
  default     = null
}

variable "repository_password" {
  type        = string
  default     = null
}

variable "repository_username" {
  type        = string
  default     = null
}


variable "wait" {
  type        = bool
  default     = null
}

variable "set" {
  type = set(object(
    {
      name  = string
      value = string
    }
  ))
  default = []
}
