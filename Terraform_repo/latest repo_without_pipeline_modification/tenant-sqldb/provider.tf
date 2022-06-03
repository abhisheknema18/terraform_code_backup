provider "azurerm" {
  features {}
}

provider "azurerm" {
  alias           = "platform"
  subscription_id = "1840866c-daf9-45f5-b823-bc446ee9c6b2"
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