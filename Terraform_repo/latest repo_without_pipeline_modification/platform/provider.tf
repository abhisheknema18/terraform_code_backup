provider "azurerm" {
  features {
      virtual_machine {
        delete_os_disk_on_deletion = true
      }

  }
}

provider "azurerm" {
  alias           = "hub"
  subscription_id = var.hub_subscription_id
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