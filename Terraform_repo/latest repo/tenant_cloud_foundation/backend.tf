terraform {
  backend "azurerm" {
    subscription_id      = "ee8d2e5f-2c43-4b48-9189-05100928e474"
    resource_group_name  = "terraform"
    storage_account_name = "tfamtfieldsmartpen"
    container_name       = "tenant_cloud_foundation"
    key                  = "internal\\dev.tfstate"
  }
}
