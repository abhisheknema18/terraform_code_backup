provider "azurerm" {
  features {
    key_vault {
      purge_soft_delete_on_destroy    = true
      recover_soft_deleted_key_vaults = true
    }
  }
}

provider "azurerm" {
  alias           = "platform"
  subscription_id = "1840866c-daf9-45f5-b823-bc446ee9c6b2"
  features {}
}

provider "azurerm" {
  alias           = "hub"
  subscription_id = "a95faac2-cab9-4d56-8c6d-fc818f9cf136"
  features {}
}

provider "azurerm" {
  alias           = "main"
  subscription_id = "355afcaa-c2a3-43f0-9b97-cdf99af5ce11"
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
