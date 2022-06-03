module "ssh_key" {
  source         = "../modules/sshkey"
  public_ssh_key = var.public_ssh_key
  ssh_private_key_filename = var.ssh_private_key_filename
  ssh_public_key_filename = var.ssh_public_key_filename
}

module "integration_ssh_key" {
  source         = "../modules/sshkey"
  public_ssh_key = var.public_ssh_key
  ssh_private_key_filename = var.ssh_integration_service_private_key_filename
  ssh_public_key_filename = var.ssh_integration_service_public_key_filename
}
