module "cluster_foundation_deploy_uksouth" {
  source = "../modules/cluster-foundation/"
  providers = {helm = helm.helm-uksouth}
  name = var.name
  chart = var.chart
  create_namespace = var.create_namespace
  namespace = var.cluster-foundation_namespace
  wait = var.wait
  repository = var.repository
  helm_version = var.chart_version
  repository_password = var.repository_password
  repository_username = var.repository_username
  set= var.set_uksouth
  depends_on = [kubernetes_secret.cluster-foundation_aks-ingress-tls_uksouth,kubernetes_secret.cluster-foundation_acr_secret_uksouth]

}

