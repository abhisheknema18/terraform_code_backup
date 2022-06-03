variable "isMultiregion" {
  type    = bool
}

variable "cluster-foundation_namespace" {
  type        = string
}

variable "chart" {
  type        = string
 }

variable "chart_version" {
  type        = string
}

variable "name" {
  type        = string
}

variable "wait" {
  type        = bool
}

variable "set_uksouth" {
 type = set(object(
    {
      name  = string
      value = string
    }
  ))
 }

variable "set_ukwest" {
  type = set(object(
  {
    name  = string
    value = string
  }
  ))

}

variable "create_namespace" {
  type        = bool

}

variable "repository" {
  type        = string
}


variable "repository_password" {
  type        = string
}

variable "repository_username" {
  type        = string
}


############################# Secert ##############################

variable "kubernetes_secret_name" {
  type        = string
}

variable "tls-crt" {
  type        = string
}
variable "tls-key" {
  type        = string
}

variable "acr_server_name" {
  type        = string
}

variable "acr_server_user_name" {
  type        = string
}

variable "acr_server_password" {
 type        = string
}


variable "prod_subscription_id" {
  type = string
  
}
variable "aks_cluster_name_uksouth" {
  type = string
 
}

variable "aks_cluster_rg_name_uksouth" {
  type = string
 
}
variable "aks_cluster_rg_name_ukwest" {
  type = string
  
}

variable "aks_cluster_name_ukwest" {
  type = string
 
}



