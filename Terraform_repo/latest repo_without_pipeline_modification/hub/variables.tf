# identifiers
variable "env" {
  type    = string
  default = "dev"
}

variable "region" {
  type    = string
  default = "uksouth"
}

variable "region_shared" {
  type    = string
  default = "shared"
}

variable "app" {
  type    = string
  default = "hub"
}

variable "network_address_spaces" {
  type    = list(string)
  default = ["192.168.42.0/23"]
}

variable "network_subnet_prefixes" {
  type    = list(string)
  default = ["192.168.42.128/26", "192.168.42.64/27", "192.168.42.0/26", "192.168.42.192/26", "192.168.43.0/26"]
}

variable "network_subnet_names" {
  type    = list(string)
  default = ["AzureFirewallSubnet", "AzureBastionSubnet", "snet-mgmt", "snet-dmz", "GatewaySubnet"]
}

variable "public_ip_conf_name" {
  type        = list(string)
  default     = ["IpConf1"]
}

variable "threat_intel_mode" {
  type        = string
  default     = "Deny"
}

variable "firewall_rule_collection_group_name" {
  type        = string
  default     = "DefaultRuleCollectionGroup"
}

variable "firewall_rule_collection_group_priority" {
  type        = string
  default     = "60000"
}

variable "network_rule_collection" {
  type = set(object(
  {
    action   = string
    name     = string
    priority = number
    rule = set(object(
    {
      destination_addresses = set(string)
      destination_fqdns     = set(string)
      destination_ip_groups = set(string)
      destination_ports     = set(string)
      name                  = string
      protocols             = set(string)
      source_addresses      = set(string)
      source_ip_groups      = set(string)
    }
    ))
  }
  ))
  default = [{
    action   = "Allow"
    name     = "ADO-RuleCollection"
    priority = 60100
    rule = [{
      destination_addresses = ["13.107.6.0/24","13.107.9.0/24","13.107.42.0/24","13.107.43.0/24"]
      destination_fqdns     = []
      destination_ip_groups = []
      destination_ports     = ["443"]
      name                  = "Outbound"
      protocols             = ["TCP"]
      source_addresses      = ["*"]
      source_ip_groups      = []
    }, {
      destination_addresses = ["*"]
      destination_fqdns     = []
      destination_ip_groups = []
      destination_ports     = ["*"]
      name                  = "Inbound"
      protocols             = ["Any"]
      source_addresses      = ["51.104.26.0/24"]
      source_ip_groups      = []
    }, {
      destination_addresses = []
      destination_fqdns     = []
      destination_ip_groups = ["ipgroup-all", "ipgroup-platform"]
      destination_ports     = ["443"]
      name                  = "HTTPS"
      protocols             = ["TCP"]
      source_addresses      = []
      source_ip_groups      = ["ipgroup-platform"]
    }
    ]
  }]
}

variable "application_rule_collection" {
  type = set(object(
  {
    action   = string
    name     = string
    priority = number
    rule = set(object(
    {
      destination_fqdn_tags = set(string)
      destination_fqdns     = set(string)
      web_categories        = set(string)
      name                  = string
      protocols = set(object(
      {
        port = number
        type = string
      }
      ))
      source_addresses = set(string)
      source_ip_groups = set(string)
    }
    ))
  }
  ))
  default = [{
    action   = "Allow"
    name     = "Common-RuleCollection"
    priority = 60200
    rule = [{
      destination_fqdn_tags = [ "AzureBackup",
                                "AppServiceEnvironment",
                                "AzureKubernetesService",
                                "HDInsight",
                                "MicrosoftActiveProtectionService",
                                "WindowsDiagnostics",
                                "WindowsUpdate",
                                "WindowsVirtualDesktop"]
      destination_fqdns     = []
      web_categories        = []
      name                  = "FQDNTags"
      protocols = [{
        port = 443
        type = "Https"
      },{
        port = 80
        type = "Http"
      }]
      source_addresses = []
      source_ip_groups = ["ipgroup-all"]
    }, {
      destination_fqdn_tags = []
      destination_fqdns     = []
      web_categories        = ["searchenginesandportals", "general"]
      name                  = "WebCategories"
      protocols = [{
        port = 443
        type = "Https"
      }]
      source_addresses = []
      source_ip_groups = ["ipgroup-all"]
    },
      {
        destination_fqdn_tags = []
        destination_fqdns     = [  "*.maven.apache.org",
                                    "*.springsource.org",
                                    "*.spring.io",
                                    "*.primefaces.org",
                                    "mcamt.eu.auth0.com",
                                    "*.microsoft.com",
                                    "*.google.com",
                                    "*.kubernetes.io",
                                    "*.k8s.io",
                                    "*.docker.com",
                                    "*.githubusercontent.com",
                                    "*.git-scm.com",
                                    "*.helm.sh",
                                    "*.github.com",
                                    "*.googleapis.com",
                                    "*.gcr.io",
                                    "*.ubuntu.com",
                                    "*.apache.stu.edu.tw",
                                    "*.maven.org",
                                    "*.alpinelinux.org",
                                    "*.hashicorp.com",
                                    "*.terraform.io",
                                    "oauth.pstmn.io"]
        web_categories        = []
        name                  = "Common-urls"
        protocols = [{
          port = 443
          type = "Https"
        },
          {
          port = 80
          type = "Http"
        }]
        source_addresses = []
        source_ip_groups = ["ipgroup-platform"]
      },
      {
        destination_fqdn_tags = []
        destination_fqdns     = ["*.fieldsmarttest.com"]
        web_categories        = []
        name                  = "domain_access_from_ado"
        protocols = [{
          port = 443
          type = "Https"
        }]
        source_addresses = []
        source_ip_groups = ["ipgroup-platform"]
      }]
  }]
}

variable "nat_rule_collection" {
  type = set(object(
  {
    action   = string
    name     = string
    priority = number
    rule = set(object(
    {
      destination_address = string
      destination_ports   = set(string)
      name                = string
      protocols           = set(string)
      source_addresses    = set(string)
      source_ip_groups    = set(string)
      translated_address  = string
      translated_port     = number
    }
    ))
  }
  ))
  default = []
}

variable "ipgroup_cidrs" {
  type        = list(set(string))
  default     = [["192.168.42.0/23", "172.16.0.0/14","10.16.0.0/14"], ["172.16.3.0/24"],["172.16.3.16/28"]]
}

variable "ipgroup_names" {
  description = "(required)"
  type        = list(string)
  default     = ["ipgroup-all", "ipgroup-platform","ipgroup-platform-ops"]
}

variable "bastion_name" {
  type        = string
  default     = "bastion-hub-dev"
}

variable "bastion_public_ip_conf_name" {
  type        = string
  default     = "IpConf1"
}

variable "bastion_public_ip_name" {
  type        = string
  default     = "pip-bastion"
}

variable "bastion_tags" {
  type = map(string)
  default = {}
}

variable "bastion_public_ip_allocation_method" {
  type        = string
  default     = "Static"
}

variable "bastion_public_ip_sku" {
  type        = string
  default     = "Standard"
}

variable "bastion_public_ip_dns" {
  type        = string
  default     = null
}