module "naming_ukwest" {
  source = "../modules/naming/"
  suffix =  [var.app, var.env, var.region_sec]
  #depends_on = [null_resource.uksouthprovisioningflag]
}