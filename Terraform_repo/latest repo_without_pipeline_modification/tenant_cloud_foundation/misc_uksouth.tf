resource "null_resource" "AzureKeyVaultSecretsProvider_uksouth" {
  provisioner "local-exec" {
    interpreter = ["sh", "-c"]
    #interpreter = ["/bin/bash", "-c"]
    command = templatefile("${path.module}/AzureKeyVaultSecretsProvider.sh.tmpl",
    {

    })
  }
  depends_on = [kubernetes_secret.cluster-foundation_aks-ingress-tls_uksouth,kubernetes_secret.cluster-foundation_acr_secret_uksouth]
}