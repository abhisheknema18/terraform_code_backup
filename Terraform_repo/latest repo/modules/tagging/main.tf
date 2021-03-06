locals {
  tags = join(" ", formatlist("%s=\"%s\"", keys(var.tags), values(var.tags)))
}

data "azurerm_client_config" "current" {}

resource "null_resource" "tags" {

  count = var.nb_resources

  triggers = {
    resource    = var.resource_ids[count.index]
    tags        = jsonencode(var.tags)
    force-apply = var.force ? uuid() : false
    behavior    = var.behavior
  }

  provisioner "local-exec" {
    interpreter = ["/bin/bash", "-c"]
    command = templatefile("${path.module}/script/azcmd.sh.tmpl",
      {
        behavior        = var.behavior
        resource_id     = var.resource_ids[count.index]
        tags            = local.tags
        subscription_id = data.azurerm_client_config.current.subscription_id
      }
    )
  }
}
