resource "helm_release" "this" {
  name = var.name
  chart = var.chart
  create_namespace = var.create_namespace
  namespace = var.namespace
  wait = var.wait
  repository = var.repository
  repository_password = var.repository_password
  repository_username = var.repository_username
  version = var.helm_version

   dynamic "set" {
    for_each = var.set
    content {
      name = set.value["name"]
      value = set.value["value"]
    }
  }



}