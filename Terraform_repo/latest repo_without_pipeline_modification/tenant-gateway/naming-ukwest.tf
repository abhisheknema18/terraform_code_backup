module "naming_ukwest" {
  source = "../modules/naming/"
  suffix = [var.app, var.env,var.region_sec, var.shared]
  depends_on = [null_resource.uksouthprovisioningflag]
}
module "naming_application_gateway_ukwest" {
  source = "../modules/naming/"
  suffix = [var.app, var.env,var.region_sec, var.customer_type]
  depends_on = [null_resource.uksouthprovisioningflag]
}

module "naming_enterprise_ukwest" {
  source = "../modules/naming/"
  suffix = [var.app, var.env,var.region_sec,var.customer_type]
}

