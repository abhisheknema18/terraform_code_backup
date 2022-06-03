resource "tls_private_key" "ssh" {
  algorithm = "RSA"
  rsa_bits  = 2048
}

resource "local_file" "private_key" {
  count    = var.public_ssh_key == "" ? 1 : 0
  content  = tls_private_key.ssh.private_key_pem
  filename = var.ssh_private_key_filename
}

resource "local_file" "public_key" {
  count    = var.ssh_public_key_filename == "" ? 0 : 1
  content  = tls_private_key.ssh.public_key_pem
  filename = var.ssh_public_key_filename
}