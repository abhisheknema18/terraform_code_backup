module "naming" {
  source = "../modules/naming/"
  suffix = [var.app, var.env,var.region,var.shared]
}
module "naming_application_gateway" {
  source = "../modules/naming/"
  suffix = [var.app, var.env,var.region, var.customer_type]
}

module "naming_enterprise" {
  source = "../modules/naming/"
  suffix = [var.app, var.env,var.region,var.customer_type]
}

module "naming_traffic_manager_profile" {
  source = "../modules/naming"
  suffix = [var.app, var.env, var.customer_type]
}