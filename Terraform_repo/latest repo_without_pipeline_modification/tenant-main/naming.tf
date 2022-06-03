module "naming" {
  source = "../modules/naming/"
  suffix = [var.app, var.env, var.region]
}

module "naming_global" {
  source = "../modules/naming/"
  suffix = [var.app_global, var.env, var.region_shared]
}