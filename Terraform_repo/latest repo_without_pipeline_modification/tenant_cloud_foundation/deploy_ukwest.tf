module "cluster_foundation_deploy_ukwest" {
  source = "../modules/cluster-foundation/"
  count                  = var.isMultiregion == true ? 1 : 0
  providers              = {helm = helm.helm-ukwest}
  name                   = var.name
  chart                  = var.chart
  create_namespace       = var.create_namespace
  namespace              = var.cluster-foundation_namespace
  wait                   = var.wait
  repository             = var.repository
  helm_version           = var.chart_version
  repository_password    = var.repository_password
  repository_username    = var.repository_username
  set                    = var.set_ukwest
  depends_on = [kubernetes_secret.cluster-foundation_aks-ingress-tls_ukwest,kubernetes_secret.cluster-foundation_acr_secret_ukwest,null_resource.uksouthprovisioningflag]

}

