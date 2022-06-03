variable "isMultiregion" {
  type    = bool
  default = true
}

variable "cluster-foundation_namespace" {
  type        = string
  default     = "kube-system"
}

variable "chart" {
  type        = string
  default     = "fusion-cluster-foundation"
}

variable "chart_version" {
  type        = string
  default     = "0.1.1"
}

variable "name" {
  type        = string
  default     = "cluster-foundation"
}

variable "wait" {
  type        = bool
  default     = true
}

variable "set_uksouth" {
 type = set(object(
    {
      name  = string
      value = string
    }
  ))
  default = [
    {
      name = "ingress-nginx.controller.service.annotations.service\\.beta\\.kubernetes\\.io/azure-load-balancer-resource-group"
      value = "MC_rg-web-cs2-uksouth_aks-web-cs2-uksouth_uksouth"
    } ,
    {
      name = "ingress-nginx.controller.service.annotations.service\\.beta\\.kubernetes\\.io/azure-load-balancer-internal"
      value = "true"
    },
    {
      name = "ingress-nginx.controller.extraArgs.default-ssl-certificate"
      value = "kube-system/aks-ingress-tls"
    },
    {
      name = "secrets-store-csi-driver.enableSecretRotation"
      value = "true"
    }

  ]
}
variable "set_ukwest" {
  type = set(object(
  {
    name  = string
    value = string
  }
  ))
  default = [
    {
      name = "ingress-nginx.controller.service.annotations.service\\.beta\\.kubernetes\\.io/azure-load-balancer-resource-group"
      value = "MC_rg-web-cs2-ukwest_aks-web-cs2-ukwest_ukwest"
    } ,
    {
      name = "ingress-nginx.controller.service.annotations.service\\.beta\\.kubernetes\\.io/azure-load-balancer-internal"
      value = "true"
    },
    {
      name = "ingress-nginx.controller.extraArgs.default-ssl-certificate"
      value = "kube-system/aks-ingress-tls"
    },
    {
      name = "secrets-store-csi-driver.enableSecretRotation"
      value = "true"
    }

  ]
}
variable "create_namespace" {
  type        = bool
  default     = false
}

variable "repository" {
  type        = string
  default     = "https://acrplatpenshared.azurecr.io/helm/v1/repo"
}


variable "repository_password" {

  type        = string
  default     = "FvKn2E5pEGCmO7j7TwDNR0q=99yB5zgC"
}

variable "repository_username" {

  type        = string
  default     = "acrplatpenshared"
}


############################# Secert ##############################

variable "kubernetes_secret_name" {
  type        = string
  default     = "aks-ingress-tls"
}

variable "tls-crt" {
  type        = string
  default     = "./certificate.crt"
}
variable "tls-key" {
  type        = string
  default     = "./domain.key"
}

variable "acr_server_name" {
  type        = string
  default     = "acrplatpenshared.azurecr.io"
}

variable "acr_server_user_name" {
  type        = string
  default     = "acrplatpenshared"
}

variable "acr_server_password" {
  type        = string
  default     = "FvKn2E5pEGCmO7j7TwDNR0q=99yB5zgC"
}


variable "prod_subscription_id" {
  type = string
  default = "c2874696-2822-4dd4-bb59-da2a3fc1aad5"
}
variable "aks_cluster_name_uksouth" {
  type = string
  default = "aks-web-cs2-uksouth"
}

variable "aks_cluster_rg_name_uksouth" {
  type = string
  default = "rg-web-cs2-uksouth"
}
variable "aks_cluster_rg_name_ukwest" {
  type = string
  default = "rg-web-cs2-ukwest"
}

variable "aks_cluster_name_ukwest" {
  type = string
  default = "aks-web-cs2-ukwest"
}



