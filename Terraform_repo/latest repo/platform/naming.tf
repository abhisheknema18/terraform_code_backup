module "naming" {
  source = "../modules/naming/"
  suffix = [var.app, var.env, var.region_shared]
}
