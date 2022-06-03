output "Uksouth_externalIP" {
  value = data.kubernetes_service_v1.uksouth_externalip.status.0.load_balancer.0.ingress.0.ip
  depends_on = [module.cluster_foundation_deploy_uksouth,data.kubernetes_service_v1.uksouth_externalip]
}

output "ukwest_externalip" {
  value      = var.isMultiregion== true ? data.kubernetes_service_v1.ukwest_externalip[0].status.0.load_balancer.0.ingress.0.ip: ""
  depends_on = [module.cluster_foundation_deploy_ukwest,data.kubernetes_service_v1.ukwest_externalip]
}