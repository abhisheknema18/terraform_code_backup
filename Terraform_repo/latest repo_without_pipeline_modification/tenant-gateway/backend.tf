terraform {
  backend "azurerm" {
    subscription_id      = "1840866c-daf9-45f5-b823-bc446ee9c6b2"
    resource_group_name  = "terraform"
    storage_account_name = "tfamtfieldsmartdev"
    container_name       = "tenant-gateway"
    key                  = "public\\lower.tfstate"
  }
}
