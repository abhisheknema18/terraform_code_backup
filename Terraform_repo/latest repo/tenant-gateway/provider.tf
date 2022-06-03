provider "azurerm" {
  features {}
}


provider "azurerm" {
  alias           = "hub"
  subscription_id = var.hub_subscription_id
  features {}
}

provider "azurerm" {
  alias           = "platform"
  subscription_id = var.plateform_subscription_id
  features {}
}

terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "2.94.0"
    }
  }
}