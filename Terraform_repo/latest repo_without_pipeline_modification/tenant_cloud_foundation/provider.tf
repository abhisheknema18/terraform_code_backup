terraform {
  required_providers {
    kubernetes = {
      source = "hashicorp/kubernetes"
      version = ">= 2.0.3"

    }
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "2.94.0"
    }
    helm = {
      source  = "hashicorp/helm"
      version = ">= 2.1.0"
    }
  }
}



provider "kubernetes" {
  alias                  = "aks-uksouth"
  host                   = data.azurerm_kubernetes_cluster.aks-uksouth.kube_config.0.host
  client_certificate     = base64decode(data.azurerm_kubernetes_cluster.aks-uksouth.kube_config.0.client_certificate)
  client_key             = base64decode(data.azurerm_kubernetes_cluster.aks-uksouth.kube_config.0.client_key)
  cluster_ca_certificate = base64decode(data.azurerm_kubernetes_cluster.aks-uksouth.kube_config.0.cluster_ca_certificate)
}

provider "helm" {
  alias                   = "helm-uksouth"
  kubernetes {
    host                   = data.azurerm_kubernetes_cluster.aks-uksouth.kube_config.0.host
    client_certificate     = base64decode(data.azurerm_kubernetes_cluster.aks-uksouth.kube_config.0.client_certificate)
    client_key             = base64decode(data.azurerm_kubernetes_cluster.aks-uksouth.kube_config.0.client_key)
    cluster_ca_certificate = base64decode(data.azurerm_kubernetes_cluster.aks-uksouth.kube_config.0.cluster_ca_certificate)
  }
}

provider "kubernetes" {
    alias                  = "aks-ukwest"
    host                   = var.isMultiregion== true ? data.azurerm_kubernetes_cluster.aks-ukwest[0].kube_config.0.host :""
    client_certificate     = var.isMultiregion== true ? base64decode(data.azurerm_kubernetes_cluster.aks-ukwest[0].kube_config.0.client_certificate) : ""
    client_key             = var.isMultiregion== true ? base64decode(data.azurerm_kubernetes_cluster.aks-ukwest[0].kube_config.0.client_key) : ""
    cluster_ca_certificate = var.isMultiregion== true ? base64decode(data.azurerm_kubernetes_cluster.aks-ukwest[0].kube_config.0.cluster_ca_certificate) :""
    }

provider "helm" {
  alias                   = "helm-ukwest"
  kubernetes {
    host                   = var.isMultiregion== true  ? data.azurerm_kubernetes_cluster.aks-ukwest[0].kube_config.0.host : ""
    client_certificate     = var.isMultiregion== true ? base64decode(data.azurerm_kubernetes_cluster.aks-ukwest[0].kube_config.0.client_certificate) : ""
    client_key             = var.isMultiregion== true ? base64decode(data.azurerm_kubernetes_cluster.aks-ukwest[0].kube_config.0.client_key) : ""
    cluster_ca_certificate = var.isMultiregion== true ? base64decode(data.azurerm_kubernetes_cluster.aks-ukwest[0].kube_config.0.cluster_ca_certificate) : ""
  }
  }


provider "azurerm" {
  features {}
}