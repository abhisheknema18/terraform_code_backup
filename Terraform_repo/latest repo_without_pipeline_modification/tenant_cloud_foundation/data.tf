data "azurerm_kubernetes_cluster" "aks-uksouth" {
  #depends_on          = [module.aks-cluster] # refresh cluster state before reading
  name                = var.aks_cluster_name_uksouth
  resource_group_name = var.aks_cluster_rg_name_uksouth
}


  data "azurerm_kubernetes_cluster" "aks-ukwest" {
    count               =var.isMultiregion == true ? 1 :0
  name                 = var.aks_cluster_name_ukwest
  resource_group_name  = var.aks_cluster_rg_name_ukwest
  }


data "kubernetes_service_v1" "uksouth_externalip" {
  provider = kubernetes.aks-uksouth
  metadata {
    name = "cluster-foundation-ingress-nginx-controller"
    namespace = var.cluster-foundation_namespace
  }
  depends_on = [module.cluster_foundation_deploy_uksouth]
}

data "kubernetes_service_v1" "ukwest_externalip" {
  count               =var.isMultiregion == true ? 1 :0
   provider = kubernetes.aks-ukwest
  metadata {
    name = "cluster-foundation-ingress-nginx-controller"
    namespace = var.cluster-foundation_namespace
  }
  depends_on = [module.cluster_foundation_deploy_ukwest]
}