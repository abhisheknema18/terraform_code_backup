terraform {
  required_providers {
    random = {
      source  = "hashicorp/random"
      version = "~> 2.2"
    }
  }
}

resource "random_string" "main" {
  length  = 60
  special = false
  upper   = false
  number  = var.unique-include-numbers
}

resource "random_string" "first_letter" {
  length  = 1
  special = false
  upper   = false
  number  = false
}



locals {
  random_safe_generation = join("", [random_string.first_letter.result, random_string.main.result])
  random                 = substr(coalesce(var.unique-seed, local.random_safe_generation), 0, var.unique-length)
  prefix                 = join("-", var.prefix)
  prefix_safe            = lower(join("", var.prefix))
  suffix                 = join("-", var.suffix)
  suffix_unique          = join("-", concat(var.suffix, [local.random]))
  suffix_safe            = lower(join("", var.suffix))
  suffix_unique_safe     = lower(join("", concat(var.suffix, [local.random])))

  az = {
    application_gateway = {
      name        = substr(join("-", compact([local.prefix, "agw", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "agw", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "agw"
      min_length  = 1
      max_length  = 80
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    application_security_group = {
      name        = substr(join("-", compact([local.prefix, "asg", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "asg", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "asg"
      min_length  = 1
      max_length  = 80
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    automation_account = {
      name        = substr(join("-", compact([local.prefix, "aa", local.suffix])), 0, 50)
      name_unique = substr(join("-", compact([local.prefix, "aa", local.suffix_unique])), 0, 50)
      dashes      = true
      slug        = "aa"
      min_length  = 6
      max_length  = 50
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z][a-zA-Z0-9-]+[a-zA-Z0-9]$"
    }
    automation_certificate = {
      name        = substr(join("-", compact([local.prefix, "aacert", local.suffix])), 0, 128)
      name_unique = substr(join("-", compact([local.prefix, "aacert", local.suffix_unique])), 0, 128)
      dashes      = true
      slug        = "aacert"
      min_length  = 1
      max_length  = 128
      scope       = "parent"
      regex       = "^[^<>*%:.?\\+\\/]+[^<>*%:.?\\+\\/ ]$"
    }
    automation_credential = {
      name        = substr(join("-", compact([local.prefix, "aacred", local.suffix])), 0, 128)
      name_unique = substr(join("-", compact([local.prefix, "aacred", local.suffix_unique])), 0, 128)
      dashes      = true
      slug        = "aacred"
      min_length  = 1
      max_length  = 128
      scope       = "parent"
      regex       = "^[^<>*%:.?\\+\\/]+[^<>*%:.?\\+\\/ ]$"
    }
    automation_runbook = {
      name        = substr(join("-", compact([local.prefix, "aacred", local.suffix])), 0, 63)
      name_unique = substr(join("-", compact([local.prefix, "aacred", local.suffix_unique])), 0, 63)
      dashes      = true
      slug        = "aacred"
      min_length  = 1
      max_length  = 63
      scope       = "parent"
      regex       = "^[a-zA-Z][a-zA-Z0-9-]+$"
    }
    automation_schedule = {
      name        = substr(join("-", compact([local.prefix, "aasched", local.suffix])), 0, 128)
      name_unique = substr(join("-", compact([local.prefix, "aasched", local.suffix_unique])), 0, 128)
      dashes      = true
      slug        = "aasched"
      min_length  = 1
      max_length  = 128
      scope       = "parent"
      regex       = "^[^<>*%:.?\\+\\/]+[^<>*%:.?\\+\\/ ]$"
    }
    automation_variable = {
      name        = substr(join("-", compact([local.prefix, "aavar", local.suffix])), 0, 128)
      name_unique = substr(join("-", compact([local.prefix, "aavar", local.suffix_unique])), 0, 128)
      dashes      = true
      slug        = "aavar"
      min_length  = 1
      max_length  = 128
      scope       = "parent"
      regex       = "^[^<>*%:.?\\+\\/]+[^<>*%:.?\\+\\/ ]$"
    }
    availability_set = {
      name        = substr(join("-", compact([local.prefix, "avail", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "avail", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "avail"
      min_length  = 1
      max_length  = 80
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-_.]+[a-zA-Z0-9_]$"
    }
    bastion_host = {
      name        = substr(join("-", compact([local.prefix, "snap", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "snap", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "snap"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    container_group = {
      name        = substr(join("-", compact([local.prefix, "cg", local.suffix])), 0, 63)
      name_unique = substr(join("-", compact([local.prefix, "cg", local.suffix_unique])), 0, 63)
      dashes      = true
      slug        = "cg"
      min_length  = 1
      max_length  = 63
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]$"
    }
    container_registry = {
      name        = substr(join("", compact([local.prefix_safe, "acr", local.suffix_safe])), 0, 63)
      name_unique = substr(join("", compact([local.prefix_safe, "acr", local.suffix_unique_safe])), 0, 63)
      dashes      = false
      slug        = "acr"
      min_length  = 1
      max_length  = 63
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9]+$"
    }
    container_registry_webhook = {
      name        = substr(join("", compact([local.prefix_safe, "crwh", local.suffix_safe])), 0, 50)
      name_unique = substr(join("", compact([local.prefix_safe, "crwh", local.suffix_unique_safe])), 0, 50)
      dashes      = false
      slug        = "crwh"
      min_length  = 1
      max_length  = 50
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9]+$"
    }
    disk_encryption_set = {
      name        = substr(join("-", compact([local.prefix, "des", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "des", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "des"
      min_length  = 1
      max_length  = 80
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9_]+$"
    }
    dns_a_record = {
      name        = substr(join("-", compact([local.prefix, "dnsrec", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "dnsrec", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "dnsrec"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    dns_aaaa_record = {
      name        = substr(join("-", compact([local.prefix, "dnsrec", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "dnsrec", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "dnsrec"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    dns_caa_record = {
      name        = substr(join("-", compact([local.prefix, "dnsrec", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "dnsrec", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "dnsrec"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    dns_cname_record = {
      name        = substr(join("-", compact([local.prefix, "dnsrec", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "dnsrec", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "dnsrec"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    dns_mx_record = {
      name        = substr(join("-", compact([local.prefix, "dnsrec", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "dnsrec", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "dnsrec"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    dns_ns_record = {
      name        = substr(join("-", compact([local.prefix, "dnsrec", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "dnsrec", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "dnsrec"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    dns_ptr_record = {
      name        = substr(join("-", compact([local.prefix, "dnsrec", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "dnsrec", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "dnsrec"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    dns_txt_record = {
      name        = substr(join("-", compact([local.prefix, "dnsrec", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "dnsrec", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "dnsrec"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    dns_zone = {
      name        = substr(join("-", compact([local.prefix, "dns", local.suffix])), 0, 63)
      name_unique = substr(join("-", compact([local.prefix, "dns", local.suffix_unique])), 0, 63)
      dashes      = true
      slug        = "dns"
      min_length  = 1
      max_length  = 63
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    firewall = {
      name        = substr(join("-", compact([local.prefix, "fw", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "fw", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "fw"
      min_length  = 1
      max_length  = 80
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    firewall_policy = {
      name        = substr(join("-", compact([local.prefix, "fwp", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "fwp", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "fwp"
      min_length  = 1
      max_length  = 80
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    firewall_application_rule_collection = {
      name        = substr(join("-", compact([local.prefix, "fwapp", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "fwapp", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "fwapp"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9\\-\\._]+[a-zA-Z0-9_]$"
    }
    firewall_ip_configuration = {
      name        = substr(join("-", compact([local.prefix, "fwipconf", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "fwipconf", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "fwipconf"
      min_length  = 1
      max_length  = 80
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9\\-\\._]+[a-zA-Z0-9_]$"
    }
    firewall_nat_rule_collection = {
      name        = substr(join("-", compact([local.prefix, "fwnatrc", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "fwnatrc", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "fwnatrc"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9\\-\\._]+[a-zA-Z0-9_]$"
    }
    firewall_network_rule_collection = {
      name        = substr(join("-", compact([local.prefix, "fwnetrc", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "fwnetrc", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "fwnetrc"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9\\-\\._]+[a-zA-Z0-9_]$"
    }
    frontdoor = {
      name        = substr(join("-", compact([local.prefix, "fd", local.suffix])), 0, 64)
      name_unique = substr(join("-", compact([local.prefix, "fd", local.suffix_unique])), 0, 64)
      dashes      = true
      slug        = "fd"
      min_length  = 5
      max_length  = 64
      scope       = "global"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]$"
    }
    frontdoor_firewall_policy = {
      name        = substr(join("-", compact([local.prefix, "fdfw", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "fdfw", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "fdfw"
      min_length  = 1
      max_length  = 80
      scope       = "global"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    image = {
      name        = substr(join("-", compact([local.prefix, "img", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "img", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "img"
      min_length  = 1
      max_length  = 80
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-_.]+[a-zA-Z0-9_]$"
    }
    key_vault = {
      name        = substr(join("-", compact([local.prefix, "kv", local.suffix])), 0, 24)
      name_unique = substr(join("-", compact([local.prefix, "kv", local.suffix_unique])), 0, 24)
      dashes      = true
      slug        = "kv"
      min_length  = 3
      max_length  = 24
      scope       = "global"
      regex       = "^[a-zA-Z][a-zA-Z0-9-]+[a-zA-Z0-9]$"
    }
    key_vault_certificate = {
      name        = substr(join("-", compact([local.prefix, "kvc", local.suffix])), 0, 127)
      name_unique = substr(join("-", compact([local.prefix, "kvc", local.suffix_unique])), 0, 127)
      dashes      = true
      slug        = "kvc"
      min_length  = 1
      max_length  = 127
      scope       = "parent"
      regex       = "^[a-zA-Z0-9-]+$"
    }
    key_vault_key = {
      name        = substr(join("-", compact([local.prefix, "kvk", local.suffix])), 0, 127)
      name_unique = substr(join("-", compact([local.prefix, "kvk", local.suffix_unique])), 0, 127)
      dashes      = true
      slug        = "kvk"
      min_length  = 1
      max_length  = 127
      scope       = "parent"
      regex       = "^[a-zA-Z0-9-]+$"
    }
    key_vault_secret = {
      name        = substr(join("-", compact([local.prefix, "kvs", local.suffix])), 0, 127)
      name_unique = substr(join("-", compact([local.prefix, "kvs", local.suffix_unique])), 0, 127)
      dashes      = true
      slug        = "kvs"
      min_length  = 1
      max_length  = 127
      scope       = "parent"
      regex       = "^[a-zA-Z0-9-]+$"
    }
    kubernetes_cluster = {
      name        = substr(join("-", compact([local.prefix, "aks", local.suffix])), 0, 63)
      name_unique = substr(join("-", compact([local.prefix, "aks", local.suffix_unique])), 0, 63)
      dashes      = true
      slug        = "aks"
      min_length  = 1
      max_length  = 63
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-_.]+[a-zA-Z0-9]$"
    }
    lb = {
      name        = substr(join("-", compact([local.prefix, "lb", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "lb", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "lb"
      min_length  = 1
      max_length  = 80
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    lb_nat_rule = {
      name        = substr(join("-", compact([local.prefix, "lbnatrl", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "lbnatrl", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "lbnatrl"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    linux_virtual_machine = {
      name        = substr(join("-", compact([local.prefix, "vm", local.suffix])), 0, 64)
      name_unique = substr(join("-", compact([local.prefix, "vm", local.suffix_unique])), 0, 64)
      dashes      = true
      slug        = "vm"
      min_length  = 1
      max_length  = 64
      scope       = "resourceGroup"
      regex       = "^[^\\/\"\\[\\]:|<>+=;,?*@&_][^\\/\"\\[\\]:|<>+=;,?*@&]+[^\\/\"\\[\\]:|<>+=;,?*@&.-]$"
    }
    linux_virtual_machine_scale_set = {
      name        = substr(join("-", compact([local.prefix, "vmss", local.suffix])), 0, 64)
      name_unique = substr(join("-", compact([local.prefix, "vmss", local.suffix_unique])), 0, 64)
      dashes      = true
      slug        = "vmss"
      min_length  = 1
      max_length  = 64
      scope       = "resourceGroup"
      regex       = "^[^\\/\"\\[\\]:|<>+=;,?*@&_][^\\/\"\\[\\]:|<>+=;,?*@&]+[^\\/\"\\[\\]:|<>+=;,?*@&.-]$"
    }
    local_network_gateway = {
      name        = substr(join("-", compact([local.prefix, "lgw", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "lgw", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "lgw"
      min_length  = 1
      max_length  = 80
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    log_analytics_workspace = {
      name        = substr(join("-", compact([local.prefix, "log", local.suffix])), 0, 63)
      name_unique = substr(join("-", compact([local.prefix, "log", local.suffix_unique])), 0, 63)
      dashes      = true
      slug        = "log"
      min_length  = 4
      max_length  = 63
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]$"
    }
    managed_disk = {
      name        = substr(join("-", compact([local.prefix, "dsk", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "dsk", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "dsk"
      min_length  = 1
      max_length  = 80
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9_]+$"
    }
    network_ddos_protection_plan = {
      name        = substr(join("-", compact([local.prefix, "ddospp", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "ddospp", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "ddospp"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    network_interface = {
      name        = substr(join("-", compact([local.prefix, "nic", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "nic", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "nic"
      min_length  = 1
      max_length  = 80
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    network_security_group = {
      name        = substr(join("-", compact([local.prefix, "nsg", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "nsg", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "nsg"
      min_length  = 1
      max_length  = 80
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    network_security_group_rule = {
      name        = substr(join("-", compact([local.prefix, "nsgr", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "nsgr", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "nsgr"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    network_security_rule = {
      name        = substr(join("-", compact([local.prefix, "nsgr", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "nsgr", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "nsgr"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    network_watcher = {
      name        = substr(join("-", compact([local.prefix, "nw", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "nw", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "nw"
      min_length  = 1
      max_length  = 80
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    point_to_site_vpn_gateway = {
      name        = substr(join("-", compact([local.prefix, "vpngw", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "vpngw", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "vpngw"
      min_length  = 1
      max_length  = 80
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    private_dns_a_record = {
      name        = substr(join("-", compact([local.prefix, "pdnsrec", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "pdnsrec", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "pdnsrec"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    private_dns_aaaa_record = {
      name        = substr(join("-", compact([local.prefix, "pdnsrec", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "pdnsrec", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "pdnsrec"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    private_dns_cname_record = {
      name        = substr(join("-", compact([local.prefix, "pdnsrec", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "pdnsrec", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "pdnsrec"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    private_dns_mx_record = {
      name        = substr(join("-", compact([local.prefix, "pdnsrec", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "pdnsrec", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "pdnsrec"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    private_dns_ptr_record = {
      name        = substr(join("-", compact([local.prefix, "pdnsrec", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "pdnsrec", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "pdnsrec"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    private_dns_srv_record = {
      name        = substr(join("-", compact([local.prefix, "pdnsrec", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "pdnsrec", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "pdnsrec"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    private_dns_txt_record = {
      name        = substr(join("-", compact([local.prefix, "pdnsrec", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "pdnsrec", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "pdnsrec"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    private_dns_zone = {
      name        = substr(join("-", compact([local.prefix, "pdns", local.suffix])), 0, 63)
      name_unique = substr(join("-", compact([local.prefix, "pdns", local.suffix_unique])), 0, 63)
      dashes      = true
      slug        = "pdns"
      min_length  = 1
      max_length  = 63
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    private_dns_zone_group = {
      name        = substr(join("-", compact([local.prefix, "pdnszg", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "pdnszg", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "pdnszg"
      min_length  = 1
      max_length  = 80
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9\\-\\._]+[a-zA-Z0-9_]$"
    }
    private_endpoint = {
      name        = substr(join("-", compact([local.prefix, "pe", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "pe", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "pe"
      min_length  = 1
      max_length  = 80
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9\\-\\._]+[a-zA-Z0-9_]$"
    }
    private_link_service = {
      name        = substr(join("-", compact([local.prefix, "pls", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "pls", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "pls"
      min_length  = 1
      max_length  = 80
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    private_service_connection = {
      name        = substr(join("-", compact([local.prefix, "psc", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "psc", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "psc"
      min_length  = 1
      max_length  = 80
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9\\-\\._]+[a-zA-Z0-9_]$"
    }
    proximity_placement_group = {
      name        = substr(join("-", compact([local.prefix, "ppg", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "ppg", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "ppg"
      min_length  = 1
      max_length  = 80
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    public_ip = {
      name        = substr(join("-", compact([local.prefix, "pip", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "pip", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "pip"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    public_ip_prefix = {
      name        = substr(join("-", compact([local.prefix, "pippf", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "pippf", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "pippf"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    resource_group = {
      name        = substr(join("-", compact([local.prefix, "rg", local.suffix])), 0, 90)
      name_unique = substr(join("-", compact([local.prefix, "rg", local.suffix_unique])), 0, 90)
      dashes      = true
      slug        = "rg"
      min_length  = 1
      max_length  = 90
      scope       = "subscription"
      regex       = "^[a-zA-Z0-9-._\\(\\)]+[a-zA-Z0-9-_\\(\\)]$"
    }
    role_assignment = {
      name        = substr(join("-", compact([local.prefix, "ra", local.suffix])), 0, 64)
      name_unique = substr(join("-", compact([local.prefix, "ra", local.suffix_unique])), 0, 64)
      dashes      = true
      slug        = "ra"
      min_length  = 1
      max_length  = 64
      scope       = "assignment"
      regex       = "^[^%]+[^ %.]$"
    }
    role_definition = {
      name        = substr(join("-", compact([local.prefix, "rd", local.suffix])), 0, 64)
      name_unique = substr(join("-", compact([local.prefix, "rd", local.suffix_unique])), 0, 64)
      dashes      = true
      slug        = "rd"
      min_length  = 1
      max_length  = 64
      scope       = "definition"
      regex       = "^[^%]+[^ %.]$"
    }
    route = {
      name        = substr(join("-", compact([local.prefix, "rt", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "rt", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "rt"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    route_table = {
      name        = substr(join("-", compact([local.prefix, "route", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "route", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "route"
      min_length  = 1
      max_length  = 80
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    shared_image = {
      name        = substr(join("-", compact([local.prefix, "si", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "si", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "si"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9]$"
    }
    shared_image_gallery = {
      name        = substr(join("", compact([local.prefix_safe, "sig", local.suffix_safe])), 0, 80)
      name_unique = substr(join("", compact([local.prefix_safe, "sig", local.suffix_unique_safe])), 0, 80)
      dashes      = false
      slug        = "sig"
      min_length  = 1
      max_length  = 80
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9.]+[a-zA-Z0-9]$"
    }
    snapshots = {
      name        = substr(join("-", compact([local.prefix, "snap", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "snap", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "snap"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    sql_elasticpool = {
      name        = substr(join("-", compact([local.prefix, "sqlep", local.suffix])), 0, 128)
      name_unique = substr(join("-", compact([local.prefix, "sqlep", local.suffix_unique])), 0, 128)
      dashes      = true
      slug        = "sqlep"
      min_length  = 1
      max_length  = 128
      scope       = "parent"
      regex       = "^[^<>*%:.?\\+\\/]+[^<>*%:.?\\+\\/ ]$"
    }
    sql_failover_group = {
      name        = substr(join("-", compact([local.prefix, "sqlfg", local.suffix])), 0, 63)
      name_unique = substr(join("-", compact([local.prefix, "sqlfg", local.suffix_unique])), 0, 63)
      dashes      = true
      slug        = "sqlfg"
      min_length  = 1
      max_length  = 63
      scope       = "global"
      regex       = "^[a-z0-9][a-z0-9-]+[a-z0-9]$"
    }
    sql_firewall_rule = {
      name        = substr(join("-", compact([local.prefix, "sqlfw", local.suffix])), 0, 128)
      name_unique = substr(join("-", compact([local.prefix, "sqlfw", local.suffix_unique])), 0, 128)
      dashes      = true
      slug        = "sqlfw"
      min_length  = 1
      max_length  = 128
      scope       = "parent"
      regex       = "^[^<>*%:?\\+\\/]+[^<>*%:.?\\+\\/]$"
    }
    sql_server = {
      name        = substr(join("-", compact([local.prefix, "sql", local.suffix])), 0, 63)
      name_unique = substr(join("-", compact([local.prefix, "sql", local.suffix_unique])), 0, 63)
      dashes      = true
      slug        = "sql"
      min_length  = 1
      max_length  = 63
      scope       = "global"
      regex       = "^[a-z0-9][a-z0-9-]+[a-z0-9]$"
    }
    storage_account = {
      name        = substr(join("", compact([local.prefix_safe, "st", local.suffix_safe])), 0, 24)
      name_unique = substr(join("", compact([local.prefix_safe, "st", local.suffix_unique_safe])), 0, 24)
      dashes      = false
      slug        = "st"
      min_length  = 3
      max_length  = 24
      scope       = "global"
      regex       = "^[a-z0-9]+$"
    }
    storage_blob = {
      name        = substr(join("-", compact([local.prefix, "blob", local.suffix])), 0, 1024)
      name_unique = substr(join("-", compact([local.prefix, "blob", local.suffix_unique])), 0, 1024)
      dashes      = true
      slug        = "blob"
      min_length  = 1
      max_length  = 1024
      scope       = "parent"
      regex       = "^[^\\s\\/$#&]+$"
    }
    storage_container = {
      name        = substr(join("-", compact([local.prefix, "stct", local.suffix])), 0, 63)
      name_unique = substr(join("-", compact([local.prefix, "stct", local.suffix_unique])), 0, 63)
      dashes      = true
      slug        = "stct"
      min_length  = 3
      max_length  = 63
      scope       = "parent"
      regex       = "^[a-z0-9][a-z0-9-]+$"
    }
    storage_queue = {
      name        = substr(join("-", compact([local.prefix, "stq", local.suffix])), 0, 63)
      name_unique = substr(join("-", compact([local.prefix, "stq", local.suffix_unique])), 0, 63)
      dashes      = true
      slug        = "stq"
      min_length  = 3
      max_length  = 63
      scope       = "parent"
      regex       = "^[a-z0-9][a-z0-9-]+[a-z0-9]$"
    }
    storage_share = {
      name        = substr(join("-", compact([local.prefix, "sts", local.suffix])), 0, 63)
      name_unique = substr(join("-", compact([local.prefix, "sts", local.suffix_unique])), 0, 63)
      dashes      = true
      slug        = "sts"
      min_length  = 3
      max_length  = 63
      scope       = "parent"
      regex       = "^[a-z0-9][a-z0-9-]+[a-z0-9]$"
    }
    storage_share_directory = {
      name        = substr(join("-", compact([local.prefix, "sts", local.suffix])), 0, 63)
      name_unique = substr(join("-", compact([local.prefix, "sts", local.suffix_unique])), 0, 63)
      dashes      = true
      slug        = "sts"
      min_length  = 3
      max_length  = 63
      scope       = "parent"
      regex       = "^[a-z0-9][a-z0-9-]+[a-z0-9]$"
    }
    storage_table = {
      name        = substr(join("-", compact([local.prefix, "stt", local.suffix])), 0, 63)
      name_unique = substr(join("-", compact([local.prefix, "stt", local.suffix_unique])), 0, 63)
      dashes      = true
      slug        = "stt"
      min_length  = 3
      max_length  = 63
      scope       = "parent"
      regex       = "^[a-z0-9][a-z0-9-]+[a-z0-9]$"
    }
    subnet = {
      name        = substr(join("-", compact([local.prefix, "snet", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "snet", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "snet"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    template_deployment = {
      name        = substr(join("-", compact([local.prefix, "deploy", local.suffix])), 0, 64)
      name_unique = substr(join("-", compact([local.prefix, "deploy", local.suffix_unique])), 0, 64)
      dashes      = true
      slug        = "deploy"
      min_length  = 1
      max_length  = 64
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9-._\\(\\)]+$"
    }
    traffic_manager_profile = {
      name        = substr(join("-", compact([local.prefix, "traf", local.suffix])), 0, 63)
      name_unique = substr(join("-", compact([local.prefix, "traf", local.suffix_unique])), 0, 63)
      dashes      = true
      slug        = "traf"
      min_length  = 1
      max_length  = 63
      scope       = "global"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-.]+[a-zA-Z0-9_]$"
    }
    virtual_machine = {
      name        = substr(join("-", compact([local.prefix, "vm", local.suffix])), 0, 15)
      name_unique = substr(join("-", compact([local.prefix, "vm", local.suffix_unique])), 0, 15)
      dashes      = true
      slug        = "vm"
      min_length  = 1
      max_length  = 15
      scope       = "resourceGroup"
      regex       = "^[^\\/\"\\[\\]:|<>+=;,?*@&_][^\\/\"\\[\\]:|<>+=;,?*@&]+[^\\/\"\\[\\]:|<>+=;,?*@&.-]$"
    }
    virtual_machine_extension = {
      name        = substr(join("-", compact([local.prefix, "vmx", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "vmx", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "vmx"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    virtual_machine_scale_set = {
      name        = substr(join("-", compact([local.prefix, "vmss", local.suffix])), 0, 15)
      name_unique = substr(join("-", compact([local.prefix, "vmss", local.suffix_unique])), 0, 15)
      dashes      = true
      slug        = "vmss"
      min_length  = 1
      max_length  = 15
      scope       = "resourceGroup"
      regex       = "^[^\\/\"\\[\\]:|<>+=;,?*@&_][^\\/\"\\[\\]:|<>+=;,?*@&]+[^\\/\"\\[\\]:|<>+=;,?*@&.-]$"
    }
    virtual_machine_scale_set_extension = {
      name        = substr(join("-", compact([local.prefix, "vmssx", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "vmssx", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "vmssx"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    virtual_network = {
      name        = substr(join("-", compact([local.prefix, "vnet", local.suffix])), 0, 64)
      name_unique = substr(join("-", compact([local.prefix, "vnet", local.suffix_unique])), 0, 64)
      dashes      = true
      slug        = "vnet"
      min_length  = 2
      max_length  = 64
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    virtual_network_gateway = {
      name        = substr(join("-", compact([local.prefix, "vgw", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "vgw", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "vgw"
      min_length  = 1
      max_length  = 80
      scope       = "resourceGroup"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    virtual_network_peering = {
      name        = substr(join("-", compact([local.prefix, "vpeer", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "vpeer", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "vpeer"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    virtual_wan = {
      name        = substr(join("-", compact([local.prefix, "vwan", local.suffix])), 0, 80)
      name_unique = substr(join("-", compact([local.prefix, "vwan", local.suffix_unique])), 0, 80)
      dashes      = true
      slug        = "vwan"
      min_length  = 1
      max_length  = 80
      scope       = "parent"
      regex       = "^[a-zA-Z0-9][a-zA-Z0-9-._]+[a-zA-Z0-9_]$"
    }
    windows_virtual_machine = {
      name        = substr(join("-", compact([local.prefix, "vm", local.suffix])), 0, 15)
      name_unique = substr(join("-", compact([local.prefix, "vm", local.suffix_unique])), 0, 15)
      dashes      = true
      slug        = "vm"
      min_length  = 1
      max_length  = 15
      scope       = "resourceGroup"
      regex       = "^[^\\/\"\\[\\]:|<>+=;,?*@&_][^\\/\"\\[\\]:|<>+=;,?*@&]+[^\\/\"\\[\\]:|<>+=;,?*@&.-]$"
    }
    windows_virtual_machine_scale_set = {
      name        = substr(join("-", compact([local.prefix, "vmss", local.suffix])), 0, 15)
      name_unique = substr(join("-", compact([local.prefix, "vmss", local.suffix_unique])), 0, 15)
      dashes      = true
      slug        = "vmss"
      min_length  = 1
      max_length  = 15
      scope       = "resourceGroup"
      regex       = "^[^\\/\"\\[\\]:|<>+=;,?*@&_][^\\/\"\\[\\]:|<>+=;,?*@&]+[^\\/\"\\[\\]:|<>+=;,?*@&.-]$"
    }
  }
  validation = {
    application_gateway = {
      valid_name        = length(regexall(local.az.application_gateway.regex, local.az.application_gateway.name)) > 0 && length(local.az.application_gateway.name) > local.az.application_gateway.min_length
      valid_name_unique = length(regexall(local.az.application_gateway.regex, local.az.application_gateway.name_unique)) > 0
    }
    application_security_group = {
      valid_name        = length(regexall(local.az.application_security_group.regex, local.az.application_security_group.name)) > 0 && length(local.az.application_security_group.name) > local.az.application_security_group.min_length
      valid_name_unique = length(regexall(local.az.application_security_group.regex, local.az.application_security_group.name_unique)) > 0
    }
    automation_account = {
      valid_name        = length(regexall(local.az.automation_account.regex, local.az.automation_account.name)) > 0 && length(local.az.automation_account.name) > local.az.automation_account.min_length
      valid_name_unique = length(regexall(local.az.automation_account.regex, local.az.automation_account.name_unique)) > 0
    }
    automation_certificate = {
      valid_name        = length(regexall(local.az.automation_certificate.regex, local.az.automation_certificate.name)) > 0 && length(local.az.automation_certificate.name) > local.az.automation_certificate.min_length
      valid_name_unique = length(regexall(local.az.automation_certificate.regex, local.az.automation_certificate.name_unique)) > 0
    }
    automation_credential = {
      valid_name        = length(regexall(local.az.automation_credential.regex, local.az.automation_credential.name)) > 0 && length(local.az.automation_credential.name) > local.az.automation_credential.min_length
      valid_name_unique = length(regexall(local.az.automation_credential.regex, local.az.automation_credential.name_unique)) > 0
    }
    automation_runbook = {
      valid_name        = length(regexall(local.az.automation_runbook.regex, local.az.automation_runbook.name)) > 0 && length(local.az.automation_runbook.name) > local.az.automation_runbook.min_length
      valid_name_unique = length(regexall(local.az.automation_runbook.regex, local.az.automation_runbook.name_unique)) > 0
    }
    automation_schedule = {
      valid_name        = length(regexall(local.az.automation_schedule.regex, local.az.automation_schedule.name)) > 0 && length(local.az.automation_schedule.name) > local.az.automation_schedule.min_length
      valid_name_unique = length(regexall(local.az.automation_schedule.regex, local.az.automation_schedule.name_unique)) > 0
    }
    automation_variable = {
      valid_name        = length(regexall(local.az.automation_variable.regex, local.az.automation_variable.name)) > 0 && length(local.az.automation_variable.name) > local.az.automation_variable.min_length
      valid_name_unique = length(regexall(local.az.automation_variable.regex, local.az.automation_variable.name_unique)) > 0
    }
    availability_set = {
      valid_name        = length(regexall(local.az.availability_set.regex, local.az.availability_set.name)) > 0 && length(local.az.availability_set.name) > local.az.availability_set.min_length
      valid_name_unique = length(regexall(local.az.availability_set.regex, local.az.availability_set.name_unique)) > 0
    }
    bastion_host = {
      valid_name        = length(regexall(local.az.bastion_host.regex, local.az.bastion_host.name)) > 0 && length(local.az.bastion_host.name) > local.az.bastion_host.min_length
      valid_name_unique = length(regexall(local.az.bastion_host.regex, local.az.bastion_host.name_unique)) > 0
    }
    container_group = {
      valid_name        = length(regexall(local.az.container_group.regex, local.az.container_group.name)) > 0 && length(local.az.container_group.name) > local.az.container_group.min_length
      valid_name_unique = length(regexall(local.az.container_group.regex, local.az.container_group.name_unique)) > 0
    }
    container_registry = {
      valid_name        = length(regexall(local.az.container_registry.regex, local.az.container_registry.name)) > 0 && length(local.az.container_registry.name) > local.az.container_registry.min_length
      valid_name_unique = length(regexall(local.az.container_registry.regex, local.az.container_registry.name_unique)) > 0
    }
    container_registry_webhook = {
      valid_name        = length(regexall(local.az.container_registry_webhook.regex, local.az.container_registry_webhook.name)) > 0 && length(local.az.container_registry_webhook.name) > local.az.container_registry_webhook.min_length
      valid_name_unique = length(regexall(local.az.container_registry_webhook.regex, local.az.container_registry_webhook.name_unique)) > 0
    }
    disk_encryption_set = {
      valid_name        = length(regexall(local.az.disk_encryption_set.regex, local.az.disk_encryption_set.name)) > 0 && length(local.az.disk_encryption_set.name) > local.az.disk_encryption_set.min_length
      valid_name_unique = length(regexall(local.az.disk_encryption_set.regex, local.az.disk_encryption_set.name_unique)) > 0
    }
    dns_a_record = {
      valid_name        = length(regexall(local.az.dns_a_record.regex, local.az.dns_a_record.name)) > 0 && length(local.az.dns_a_record.name) > local.az.dns_a_record.min_length
      valid_name_unique = length(regexall(local.az.dns_a_record.regex, local.az.dns_a_record.name_unique)) > 0
    }
    dns_aaaa_record = {
      valid_name        = length(regexall(local.az.dns_aaaa_record.regex, local.az.dns_aaaa_record.name)) > 0 && length(local.az.dns_aaaa_record.name) > local.az.dns_aaaa_record.min_length
      valid_name_unique = length(regexall(local.az.dns_aaaa_record.regex, local.az.dns_aaaa_record.name_unique)) > 0
    }
    dns_caa_record = {
      valid_name        = length(regexall(local.az.dns_caa_record.regex, local.az.dns_caa_record.name)) > 0 && length(local.az.dns_caa_record.name) > local.az.dns_caa_record.min_length
      valid_name_unique = length(regexall(local.az.dns_caa_record.regex, local.az.dns_caa_record.name_unique)) > 0
    }
    dns_cname_record = {
      valid_name        = length(regexall(local.az.dns_cname_record.regex, local.az.dns_cname_record.name)) > 0 && length(local.az.dns_cname_record.name) > local.az.dns_cname_record.min_length
      valid_name_unique = length(regexall(local.az.dns_cname_record.regex, local.az.dns_cname_record.name_unique)) > 0
    }
    dns_mx_record = {
      valid_name        = length(regexall(local.az.dns_mx_record.regex, local.az.dns_mx_record.name)) > 0 && length(local.az.dns_mx_record.name) > local.az.dns_mx_record.min_length
      valid_name_unique = length(regexall(local.az.dns_mx_record.regex, local.az.dns_mx_record.name_unique)) > 0
    }
    dns_ns_record = {
      valid_name        = length(regexall(local.az.dns_ns_record.regex, local.az.dns_ns_record.name)) > 0 && length(local.az.dns_ns_record.name) > local.az.dns_ns_record.min_length
      valid_name_unique = length(regexall(local.az.dns_ns_record.regex, local.az.dns_ns_record.name_unique)) > 0
    }
    dns_ptr_record = {
      valid_name        = length(regexall(local.az.dns_ptr_record.regex, local.az.dns_ptr_record.name)) > 0 && length(local.az.dns_ptr_record.name) > local.az.dns_ptr_record.min_length
      valid_name_unique = length(regexall(local.az.dns_ptr_record.regex, local.az.dns_ptr_record.name_unique)) > 0
    }
    dns_txt_record = {
      valid_name        = length(regexall(local.az.dns_txt_record.regex, local.az.dns_txt_record.name)) > 0 && length(local.az.dns_txt_record.name) > local.az.dns_txt_record.min_length
      valid_name_unique = length(regexall(local.az.dns_txt_record.regex, local.az.dns_txt_record.name_unique)) > 0
    }
    dns_zone = {
      valid_name        = length(regexall(local.az.dns_zone.regex, local.az.dns_zone.name)) > 0 && length(local.az.dns_zone.name) > local.az.dns_zone.min_length
      valid_name_unique = length(regexall(local.az.dns_zone.regex, local.az.dns_zone.name_unique)) > 0
    }
    firewall = {
      valid_name        = length(regexall(local.az.firewall.regex, local.az.firewall.name)) > 0 && length(local.az.firewall.name) > local.az.firewall.min_length
      valid_name_unique = length(regexall(local.az.firewall.regex, local.az.firewall.name_unique)) > 0
    }
    firewall_policy = {
      valid_name        = length(regexall(local.az.firewall_policy.regex, local.az.firewall_policy.name)) > 0 && length(local.az.firewall_policy.name) > local.az.firewall_policy.min_length
      valid_name_unique = length(regexall(local.az.firewall_policy.regex, local.az.firewall_policy.name_unique)) > 0
    }
    firewall_application_rule_collection = {
      valid_name        = length(regexall(local.az.firewall_application_rule_collection.regex, local.az.firewall_application_rule_collection.name)) > 0 && length(local.az.firewall_application_rule_collection.name) > local.az.firewall_application_rule_collection.min_length
      valid_name_unique = length(regexall(local.az.firewall_application_rule_collection.regex, local.az.firewall_application_rule_collection.name_unique)) > 0
    }
    firewall_ip_configuration = {
      valid_name        = length(regexall(local.az.firewall_ip_configuration.regex, local.az.firewall_ip_configuration.name)) > 0 && length(local.az.firewall_ip_configuration.name) > local.az.firewall_ip_configuration.min_length
      valid_name_unique = length(regexall(local.az.firewall_ip_configuration.regex, local.az.firewall_ip_configuration.name_unique)) > 0
    }
    firewall_nat_rule_collection = {
      valid_name        = length(regexall(local.az.firewall_nat_rule_collection.regex, local.az.firewall_nat_rule_collection.name)) > 0 && length(local.az.firewall_nat_rule_collection.name) > local.az.firewall_nat_rule_collection.min_length
      valid_name_unique = length(regexall(local.az.firewall_nat_rule_collection.regex, local.az.firewall_nat_rule_collection.name_unique)) > 0
    }
    firewall_network_rule_collection = {
      valid_name        = length(regexall(local.az.firewall_network_rule_collection.regex, local.az.firewall_network_rule_collection.name)) > 0 && length(local.az.firewall_network_rule_collection.name) > local.az.firewall_network_rule_collection.min_length
      valid_name_unique = length(regexall(local.az.firewall_network_rule_collection.regex, local.az.firewall_network_rule_collection.name_unique)) > 0
    }
    frontdoor = {
      valid_name        = length(regexall(local.az.frontdoor.regex, local.az.frontdoor.name)) > 0 && length(local.az.frontdoor.name) > local.az.frontdoor.min_length
      valid_name_unique = length(regexall(local.az.frontdoor.regex, local.az.frontdoor.name_unique)) > 0
    }
    frontdoor_firewall_policy = {
      valid_name        = length(regexall(local.az.frontdoor_firewall_policy.regex, local.az.frontdoor_firewall_policy.name)) > 0 && length(local.az.frontdoor_firewall_policy.name) > local.az.frontdoor_firewall_policy.min_length
      valid_name_unique = length(regexall(local.az.frontdoor_firewall_policy.regex, local.az.frontdoor_firewall_policy.name_unique)) > 0
    }
    image = {
      valid_name        = length(regexall(local.az.image.regex, local.az.image.name)) > 0 && length(local.az.image.name) > local.az.image.min_length
      valid_name_unique = length(regexall(local.az.image.regex, local.az.image.name_unique)) > 0
    }
    key_vault = {
      valid_name        = length(regexall(local.az.key_vault.regex, local.az.key_vault.name)) > 0 && length(local.az.key_vault.name) > local.az.key_vault.min_length
      valid_name_unique = length(regexall(local.az.key_vault.regex, local.az.key_vault.name_unique)) > 0
    }
    key_vault_certificate = {
      valid_name        = length(regexall(local.az.key_vault_certificate.regex, local.az.key_vault_certificate.name)) > 0 && length(local.az.key_vault_certificate.name) > local.az.key_vault_certificate.min_length
      valid_name_unique = length(regexall(local.az.key_vault_certificate.regex, local.az.key_vault_certificate.name_unique)) > 0
    }
    key_vault_key = {
      valid_name        = length(regexall(local.az.key_vault_key.regex, local.az.key_vault_key.name)) > 0 && length(local.az.key_vault_key.name) > local.az.key_vault_key.min_length
      valid_name_unique = length(regexall(local.az.key_vault_key.regex, local.az.key_vault_key.name_unique)) > 0
    }
    key_vault_secret = {
      valid_name        = length(regexall(local.az.key_vault_secret.regex, local.az.key_vault_secret.name)) > 0 && length(local.az.key_vault_secret.name) > local.az.key_vault_secret.min_length
      valid_name_unique = length(regexall(local.az.key_vault_secret.regex, local.az.key_vault_secret.name_unique)) > 0
    }
    kubernetes_cluster = {
      valid_name        = length(regexall(local.az.kubernetes_cluster.regex, local.az.kubernetes_cluster.name)) > 0 && length(local.az.kubernetes_cluster.name) > local.az.kubernetes_cluster.min_length
      valid_name_unique = length(regexall(local.az.kubernetes_cluster.regex, local.az.kubernetes_cluster.name_unique)) > 0
    }
    lb = {
      valid_name        = length(regexall(local.az.lb.regex, local.az.lb.name)) > 0 && length(local.az.lb.name) > local.az.lb.min_length
      valid_name_unique = length(regexall(local.az.lb.regex, local.az.lb.name_unique)) > 0
    }
    lb_nat_rule = {
      valid_name        = length(regexall(local.az.lb_nat_rule.regex, local.az.lb_nat_rule.name)) > 0 && length(local.az.lb_nat_rule.name) > local.az.lb_nat_rule.min_length
      valid_name_unique = length(regexall(local.az.lb_nat_rule.regex, local.az.lb_nat_rule.name_unique)) > 0
    }
    linux_virtual_machine = {
      valid_name        = length(regexall(local.az.linux_virtual_machine.regex, local.az.linux_virtual_machine.name)) > 0 && length(local.az.linux_virtual_machine.name) > local.az.linux_virtual_machine.min_length
      valid_name_unique = length(regexall(local.az.linux_virtual_machine.regex, local.az.linux_virtual_machine.name_unique)) > 0
    }
    linux_virtual_machine_scale_set = {
      valid_name        = length(regexall(local.az.linux_virtual_machine_scale_set.regex, local.az.linux_virtual_machine_scale_set.name)) > 0 && length(local.az.linux_virtual_machine_scale_set.name) > local.az.linux_virtual_machine_scale_set.min_length
      valid_name_unique = length(regexall(local.az.linux_virtual_machine_scale_set.regex, local.az.linux_virtual_machine_scale_set.name_unique)) > 0
    }
    local_network_gateway = {
      valid_name        = length(regexall(local.az.local_network_gateway.regex, local.az.local_network_gateway.name)) > 0 && length(local.az.local_network_gateway.name) > local.az.local_network_gateway.min_length
      valid_name_unique = length(regexall(local.az.local_network_gateway.regex, local.az.local_network_gateway.name_unique)) > 0
    }
    log_analytics_workspace = {
      valid_name        = length(regexall(local.az.log_analytics_workspace.regex, local.az.log_analytics_workspace.name)) > 0 && length(local.az.log_analytics_workspace.name) > local.az.log_analytics_workspace.min_length
      valid_name_unique = length(regexall(local.az.log_analytics_workspace.regex, local.az.log_analytics_workspace.name_unique)) > 0
    }
    managed_disk = {
      valid_name        = length(regexall(local.az.managed_disk.regex, local.az.managed_disk.name)) > 0 && length(local.az.managed_disk.name) > local.az.managed_disk.min_length
      valid_name_unique = length(regexall(local.az.managed_disk.regex, local.az.managed_disk.name_unique)) > 0
    }
    network_ddos_protection_plan = {
      valid_name        = length(regexall(local.az.network_ddos_protection_plan.regex, local.az.network_ddos_protection_plan.name)) > 0 && length(local.az.network_ddos_protection_plan.name) > local.az.network_ddos_protection_plan.min_length
      valid_name_unique = length(regexall(local.az.network_ddos_protection_plan.regex, local.az.network_ddos_protection_plan.name_unique)) > 0
    }
    network_interface = {
      valid_name        = length(regexall(local.az.network_interface.regex, local.az.network_interface.name)) > 0 && length(local.az.network_interface.name) > local.az.network_interface.min_length
      valid_name_unique = length(regexall(local.az.network_interface.regex, local.az.network_interface.name_unique)) > 0
    }
    network_security_group = {
      valid_name        = length(regexall(local.az.network_security_group.regex, local.az.network_security_group.name)) > 0 && length(local.az.network_security_group.name) > local.az.network_security_group.min_length
      valid_name_unique = length(regexall(local.az.network_security_group.regex, local.az.network_security_group.name_unique)) > 0
    }
    network_security_group_rule = {
      valid_name        = length(regexall(local.az.network_security_group_rule.regex, local.az.network_security_group_rule.name)) > 0 && length(local.az.network_security_group_rule.name) > local.az.network_security_group_rule.min_length
      valid_name_unique = length(regexall(local.az.network_security_group_rule.regex, local.az.network_security_group_rule.name_unique)) > 0
    }
    network_security_rule = {
      valid_name        = length(regexall(local.az.network_security_rule.regex, local.az.network_security_rule.name)) > 0 && length(local.az.network_security_rule.name) > local.az.network_security_rule.min_length
      valid_name_unique = length(regexall(local.az.network_security_rule.regex, local.az.network_security_rule.name_unique)) > 0
    }
    network_watcher = {
      valid_name        = length(regexall(local.az.network_watcher.regex, local.az.network_watcher.name)) > 0 && length(local.az.network_watcher.name) > local.az.network_watcher.min_length
      valid_name_unique = length(regexall(local.az.network_watcher.regex, local.az.network_watcher.name_unique)) > 0
    }
    point_to_site_vpn_gateway = {
      valid_name        = length(regexall(local.az.point_to_site_vpn_gateway.regex, local.az.point_to_site_vpn_gateway.name)) > 0 && length(local.az.point_to_site_vpn_gateway.name) > local.az.point_to_site_vpn_gateway.min_length
      valid_name_unique = length(regexall(local.az.point_to_site_vpn_gateway.regex, local.az.point_to_site_vpn_gateway.name_unique)) > 0
    }
    private_dns_a_record = {
      valid_name        = length(regexall(local.az.private_dns_a_record.regex, local.az.private_dns_a_record.name)) > 0 && length(local.az.private_dns_a_record.name) > local.az.private_dns_a_record.min_length
      valid_name_unique = length(regexall(local.az.private_dns_a_record.regex, local.az.private_dns_a_record.name_unique)) > 0
    }
    private_dns_aaaa_record = {
      valid_name        = length(regexall(local.az.private_dns_aaaa_record.regex, local.az.private_dns_aaaa_record.name)) > 0 && length(local.az.private_dns_aaaa_record.name) > local.az.private_dns_aaaa_record.min_length
      valid_name_unique = length(regexall(local.az.private_dns_aaaa_record.regex, local.az.private_dns_aaaa_record.name_unique)) > 0
    }
    private_dns_cname_record = {
      valid_name        = length(regexall(local.az.private_dns_cname_record.regex, local.az.private_dns_cname_record.name)) > 0 && length(local.az.private_dns_cname_record.name) > local.az.private_dns_cname_record.min_length
      valid_name_unique = length(regexall(local.az.private_dns_cname_record.regex, local.az.private_dns_cname_record.name_unique)) > 0
    }
    private_dns_mx_record = {
      valid_name        = length(regexall(local.az.private_dns_mx_record.regex, local.az.private_dns_mx_record.name)) > 0 && length(local.az.private_dns_mx_record.name) > local.az.private_dns_mx_record.min_length
      valid_name_unique = length(regexall(local.az.private_dns_mx_record.regex, local.az.private_dns_mx_record.name_unique)) > 0
    }
    private_dns_ptr_record = {
      valid_name        = length(regexall(local.az.private_dns_ptr_record.regex, local.az.private_dns_ptr_record.name)) > 0 && length(local.az.private_dns_ptr_record.name) > local.az.private_dns_ptr_record.min_length
      valid_name_unique = length(regexall(local.az.private_dns_ptr_record.regex, local.az.private_dns_ptr_record.name_unique)) > 0
    }
    private_dns_srv_record = {
      valid_name        = length(regexall(local.az.private_dns_srv_record.regex, local.az.private_dns_srv_record.name)) > 0 && length(local.az.private_dns_srv_record.name) > local.az.private_dns_srv_record.min_length
      valid_name_unique = length(regexall(local.az.private_dns_srv_record.regex, local.az.private_dns_srv_record.name_unique)) > 0
    }
    private_dns_txt_record = {
      valid_name        = length(regexall(local.az.private_dns_txt_record.regex, local.az.private_dns_txt_record.name)) > 0 && length(local.az.private_dns_txt_record.name) > local.az.private_dns_txt_record.min_length
      valid_name_unique = length(regexall(local.az.private_dns_txt_record.regex, local.az.private_dns_txt_record.name_unique)) > 0
    }
    private_dns_zone = {
      valid_name        = length(regexall(local.az.private_dns_zone.regex, local.az.private_dns_zone.name)) > 0 && length(local.az.private_dns_zone.name) > local.az.private_dns_zone.min_length
      valid_name_unique = length(regexall(local.az.private_dns_zone.regex, local.az.private_dns_zone.name_unique)) > 0
    }
    private_dns_zone_group = {
      valid_name        = length(regexall(local.az.private_dns_zone_group.regex, local.az.private_dns_zone_group.name)) > 0 && length(local.az.private_dns_zone_group.name) > local.az.private_dns_zone_group.min_length
      valid_name_unique = length(regexall(local.az.private_dns_zone_group.regex, local.az.private_dns_zone_group.name_unique)) > 0
    }
    private_endpoint = {
      valid_name        = length(regexall(local.az.private_endpoint.regex, local.az.private_endpoint.name)) > 0 && length(local.az.private_endpoint.name) > local.az.private_endpoint.min_length
      valid_name_unique = length(regexall(local.az.private_endpoint.regex, local.az.private_endpoint.name_unique)) > 0
    }
    private_link_service = {
      valid_name        = length(regexall(local.az.private_link_service.regex, local.az.private_link_service.name)) > 0 && length(local.az.private_link_service.name) > local.az.private_link_service.min_length
      valid_name_unique = length(regexall(local.az.private_link_service.regex, local.az.private_link_service.name_unique)) > 0
    }
    private_service_connection = {
      valid_name        = length(regexall(local.az.private_service_connection.regex, local.az.private_service_connection.name)) > 0 && length(local.az.private_service_connection.name) > local.az.private_service_connection.min_length
      valid_name_unique = length(regexall(local.az.private_service_connection.regex, local.az.private_service_connection.name_unique)) > 0
    }
    proximity_placement_group = {
      valid_name        = length(regexall(local.az.proximity_placement_group.regex, local.az.proximity_placement_group.name)) > 0 && length(local.az.proximity_placement_group.name) > local.az.proximity_placement_group.min_length
      valid_name_unique = length(regexall(local.az.proximity_placement_group.regex, local.az.proximity_placement_group.name_unique)) > 0
    }
    public_ip = {
      valid_name        = length(regexall(local.az.public_ip.regex, local.az.public_ip.name)) > 0 && length(local.az.public_ip.name) > local.az.public_ip.min_length
      valid_name_unique = length(regexall(local.az.public_ip.regex, local.az.public_ip.name_unique)) > 0
    }
    public_ip_prefix = {
      valid_name        = length(regexall(local.az.public_ip_prefix.regex, local.az.public_ip_prefix.name)) > 0 && length(local.az.public_ip_prefix.name) > local.az.public_ip_prefix.min_length
      valid_name_unique = length(regexall(local.az.public_ip_prefix.regex, local.az.public_ip_prefix.name_unique)) > 0
    }
    resource_group = {
      valid_name        = length(regexall(local.az.resource_group.regex, local.az.resource_group.name)) > 0 && length(local.az.resource_group.name) > local.az.resource_group.min_length
      valid_name_unique = length(regexall(local.az.resource_group.regex, local.az.resource_group.name_unique)) > 0
    }
    role_assignment = {
      valid_name        = length(regexall(local.az.role_assignment.regex, local.az.role_assignment.name)) > 0 && length(local.az.role_assignment.name) > local.az.role_assignment.min_length
      valid_name_unique = length(regexall(local.az.role_assignment.regex, local.az.role_assignment.name_unique)) > 0
    }
    role_definition = {
      valid_name        = length(regexall(local.az.role_definition.regex, local.az.role_definition.name)) > 0 && length(local.az.role_definition.name) > local.az.role_definition.min_length
      valid_name_unique = length(regexall(local.az.role_definition.regex, local.az.role_definition.name_unique)) > 0
    }
    route = {
      valid_name        = length(regexall(local.az.route.regex, local.az.route.name)) > 0 && length(local.az.route.name) > local.az.route.min_length
      valid_name_unique = length(regexall(local.az.route.regex, local.az.route.name_unique)) > 0
    }
    route_table = {
      valid_name        = length(regexall(local.az.route_table.regex, local.az.route_table.name)) > 0 && length(local.az.route_table.name) > local.az.route_table.min_length
      valid_name_unique = length(regexall(local.az.route_table.regex, local.az.route_table.name_unique)) > 0
    }
    shared_image = {
      valid_name        = length(regexall(local.az.shared_image.regex, local.az.shared_image.name)) > 0 && length(local.az.shared_image.name) > local.az.shared_image.min_length
      valid_name_unique = length(regexall(local.az.shared_image.regex, local.az.shared_image.name_unique)) > 0
    }
    shared_image_gallery = {
      valid_name        = length(regexall(local.az.shared_image_gallery.regex, local.az.shared_image_gallery.name)) > 0 && length(local.az.shared_image_gallery.name) > local.az.shared_image_gallery.min_length
      valid_name_unique = length(regexall(local.az.shared_image_gallery.regex, local.az.shared_image_gallery.name_unique)) > 0
    }
    snapshots = {
      valid_name        = length(regexall(local.az.snapshots.regex, local.az.snapshots.name)) > 0 && length(local.az.snapshots.name) > local.az.snapshots.min_length
      valid_name_unique = length(regexall(local.az.snapshots.regex, local.az.snapshots.name_unique)) > 0
    }
    sql_elasticpool = {
      valid_name        = length(regexall(local.az.sql_elasticpool.regex, local.az.sql_elasticpool.name)) > 0 && length(local.az.sql_elasticpool.name) > local.az.sql_elasticpool.min_length
      valid_name_unique = length(regexall(local.az.sql_elasticpool.regex, local.az.sql_elasticpool.name_unique)) > 0
    }
    sql_failover_group = {
      valid_name        = length(regexall(local.az.sql_failover_group.regex, local.az.sql_failover_group.name)) > 0 && length(local.az.sql_failover_group.name) > local.az.sql_failover_group.min_length
      valid_name_unique = length(regexall(local.az.sql_failover_group.regex, local.az.sql_failover_group.name_unique)) > 0
    }
    sql_firewall_rule = {
      valid_name        = length(regexall(local.az.sql_firewall_rule.regex, local.az.sql_firewall_rule.name)) > 0 && length(local.az.sql_firewall_rule.name) > local.az.sql_firewall_rule.min_length
      valid_name_unique = length(regexall(local.az.sql_firewall_rule.regex, local.az.sql_firewall_rule.name_unique)) > 0
    }
    sql_server = {
      valid_name        = length(regexall(local.az.sql_server.regex, local.az.sql_server.name)) > 0 && length(local.az.sql_server.name) > local.az.sql_server.min_length
      valid_name_unique = length(regexall(local.az.sql_server.regex, local.az.sql_server.name_unique)) > 0
    }
    storage_account = {
      valid_name        = length(regexall(local.az.storage_account.regex, local.az.storage_account.name)) > 0 && length(local.az.storage_account.name) > local.az.storage_account.min_length
      valid_name_unique = length(regexall(local.az.storage_account.regex, local.az.storage_account.name_unique)) > 0
    }
    storage_blob = {
      valid_name        = length(regexall(local.az.storage_blob.regex, local.az.storage_blob.name)) > 0 && length(local.az.storage_blob.name) > local.az.storage_blob.min_length
      valid_name_unique = length(regexall(local.az.storage_blob.regex, local.az.storage_blob.name_unique)) > 0
    }
    storage_container = {
      valid_name        = length(regexall(local.az.storage_container.regex, local.az.storage_container.name)) > 0 && length(local.az.storage_container.name) > local.az.storage_container.min_length
      valid_name_unique = length(regexall(local.az.storage_container.regex, local.az.storage_container.name_unique)) > 0
    }
    storage_queue = {
      valid_name        = length(regexall(local.az.storage_queue.regex, local.az.storage_queue.name)) > 0 && length(local.az.storage_queue.name) > local.az.storage_queue.min_length
      valid_name_unique = length(regexall(local.az.storage_queue.regex, local.az.storage_queue.name_unique)) > 0
    }
    storage_share = {
      valid_name        = length(regexall(local.az.storage_share.regex, local.az.storage_share.name)) > 0 && length(local.az.storage_share.name) > local.az.storage_share.min_length
      valid_name_unique = length(regexall(local.az.storage_share.regex, local.az.storage_share.name_unique)) > 0
    }
    storage_share_directory = {
      valid_name        = length(regexall(local.az.storage_share_directory.regex, local.az.storage_share_directory.name)) > 0 && length(local.az.storage_share_directory.name) > local.az.storage_share_directory.min_length
      valid_name_unique = length(regexall(local.az.storage_share_directory.regex, local.az.storage_share_directory.name_unique)) > 0
    }
    storage_table = {
      valid_name        = length(regexall(local.az.storage_table.regex, local.az.storage_table.name)) > 0 && length(local.az.storage_table.name) > local.az.storage_table.min_length
      valid_name_unique = length(regexall(local.az.storage_table.regex, local.az.storage_table.name_unique)) > 0
    }
    subnet = {
      valid_name        = length(regexall(local.az.subnet.regex, local.az.subnet.name)) > 0 && length(local.az.subnet.name) > local.az.subnet.min_length
      valid_name_unique = length(regexall(local.az.subnet.regex, local.az.subnet.name_unique)) > 0
    }
    template_deployment = {
      valid_name        = length(regexall(local.az.template_deployment.regex, local.az.template_deployment.name)) > 0 && length(local.az.template_deployment.name) > local.az.template_deployment.min_length
      valid_name_unique = length(regexall(local.az.template_deployment.regex, local.az.template_deployment.name_unique)) > 0
    }
    traffic_manager_profile = {
      valid_name        = length(regexall(local.az.traffic_manager_profile.regex, local.az.traffic_manager_profile.name)) > 0 && length(local.az.traffic_manager_profile.name) > local.az.traffic_manager_profile.min_length
      valid_name_unique = length(regexall(local.az.traffic_manager_profile.regex, local.az.traffic_manager_profile.name_unique)) > 0
    }
    virtual_machine = {
      valid_name        = length(regexall(local.az.virtual_machine.regex, local.az.virtual_machine.name)) > 0 && length(local.az.virtual_machine.name) > local.az.virtual_machine.min_length
      valid_name_unique = length(regexall(local.az.virtual_machine.regex, local.az.virtual_machine.name_unique)) > 0
    }
    virtual_machine_extension = {
      valid_name        = length(regexall(local.az.virtual_machine_extension.regex, local.az.virtual_machine_extension.name)) > 0 && length(local.az.virtual_machine_extension.name) > local.az.virtual_machine_extension.min_length
      valid_name_unique = length(regexall(local.az.virtual_machine_extension.regex, local.az.virtual_machine_extension.name_unique)) > 0
    }
    virtual_machine_scale_set = {
      valid_name        = length(regexall(local.az.virtual_machine_scale_set.regex, local.az.virtual_machine_scale_set.name)) > 0 && length(local.az.virtual_machine_scale_set.name) > local.az.virtual_machine_scale_set.min_length
      valid_name_unique = length(regexall(local.az.virtual_machine_scale_set.regex, local.az.virtual_machine_scale_set.name_unique)) > 0
    }
    virtual_machine_scale_set_extension = {
      valid_name        = length(regexall(local.az.virtual_machine_scale_set_extension.regex, local.az.virtual_machine_scale_set_extension.name)) > 0 && length(local.az.virtual_machine_scale_set_extension.name) > local.az.virtual_machine_scale_set_extension.min_length
      valid_name_unique = length(regexall(local.az.virtual_machine_scale_set_extension.regex, local.az.virtual_machine_scale_set_extension.name_unique)) > 0
    }
    virtual_network = {
      valid_name        = length(regexall(local.az.virtual_network.regex, local.az.virtual_network.name)) > 0 && length(local.az.virtual_network.name) > local.az.virtual_network.min_length
      valid_name_unique = length(regexall(local.az.virtual_network.regex, local.az.virtual_network.name_unique)) > 0
    }
    virtual_network_gateway = {
      valid_name        = length(regexall(local.az.virtual_network_gateway.regex, local.az.virtual_network_gateway.name)) > 0 && length(local.az.virtual_network_gateway.name) > local.az.virtual_network_gateway.min_length
      valid_name_unique = length(regexall(local.az.virtual_network_gateway.regex, local.az.virtual_network_gateway.name_unique)) > 0
    }
    virtual_network_peering = {
      valid_name        = length(regexall(local.az.virtual_network_peering.regex, local.az.virtual_network_peering.name)) > 0 && length(local.az.virtual_network_peering.name) > local.az.virtual_network_peering.min_length
      valid_name_unique = length(regexall(local.az.virtual_network_peering.regex, local.az.virtual_network_peering.name_unique)) > 0
    }
    virtual_wan = {
      valid_name        = length(regexall(local.az.virtual_wan.regex, local.az.virtual_wan.name)) > 0 && length(local.az.virtual_wan.name) > local.az.virtual_wan.min_length
      valid_name_unique = length(regexall(local.az.virtual_wan.regex, local.az.virtual_wan.name_unique)) > 0
    }
    windows_virtual_machine = {
      valid_name        = length(regexall(local.az.windows_virtual_machine.regex, local.az.windows_virtual_machine.name)) > 0 && length(local.az.windows_virtual_machine.name) > local.az.windows_virtual_machine.min_length
      valid_name_unique = length(regexall(local.az.windows_virtual_machine.regex, local.az.windows_virtual_machine.name_unique)) > 0
    }
    windows_virtual_machine_scale_set = {
      valid_name        = length(regexall(local.az.windows_virtual_machine_scale_set.regex, local.az.windows_virtual_machine_scale_set.name)) > 0 && length(local.az.windows_virtual_machine_scale_set.name) > local.az.windows_virtual_machine_scale_set.min_length
      valid_name_unique = length(regexall(local.az.windows_virtual_machine_scale_set.regex, local.az.windows_virtual_machine_scale_set.name_unique)) > 0
    }
  }
}
