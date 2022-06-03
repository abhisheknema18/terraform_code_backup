resource "kubernetes_secret" "cluster-foundation_aks-ingress-tls_uksouth" {
  provider = kubernetes.aks-uksouth
  metadata {
    name = var.kubernetes_secret_name
    namespace = var.cluster-foundation_namespace
  }
  data = {
    "tls.crt" = pathexpand(var.tls-crt)
    "tls.key" = pathexpand(var.tls-key)
  }
  type = "kubernetes.io/tls"
}

resource "kubernetes_secret" "cluster-foundation_acr_secret_uksouth" {
  provider = kubernetes.aks-uksouth
  metadata {
    name = "acr-secret"
    namespace = var.cluster-foundation_namespace
  }

  data = {
    ".dockerconfigjson" = jsonencode({
      auths = {
        "${var.acr_server_name}" = {
          auth = "${base64encode("${var.acr_server_user_name}:${var.acr_server_password}")}"
        }
      }
    })

  }
  type = "kubernetes.io/dockerconfigjson"
}
