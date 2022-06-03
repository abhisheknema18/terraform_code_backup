module "naming" {
  source = "../modules/naming/"
  suffix = [var.app, var.env, var.region]
}

module "naming_sec" {
  source = "../modules/naming/"
  suffix = [var.app, var.env, var.region_sec]
}

module "naming_shared" {
  source = "../modules/naming/"
  suffix = [var.app_type, var.env, var.region_shared]
}