resource "null_resource" "AzureKeyVaultSecretsProvider_ukwest" {
  count                            = var.isMultiregion == true ? 1 : 0
  provisioner "local-exec" {
    interpreter = ["sh", "-c"]
    #interpreter = ["/bin/bash", "-c"]
    command = templatefile("${path.module}/AzureKeyVaultSecretsProvider.sh.tmpl",
    {

    })
  }
  depends_on = [kubernetes_secret.cluster-foundation_aks-ingress-tls_ukwest,kubernetes_secret.cluster-foundation_acr_secret_ukwest,null_resource.uksouthprovisioningflag]
}


resource "null_resource" "uksouthprovisioningflag" {
  count                            = var.isMultiregion == true ? 1 : 0
  depends_on = [kubernetes_secret.cluster-foundation_aks-ingress-tls_uksouth,kubernetes_secret.cluster-foundation_acr_secret_uksouth,module.cluster_foundation_deploy_uksouth,null_resource.AzureKeyVaultSecretsProvider_uksouth]
}
