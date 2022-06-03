output "public_ssh_key" {
  value = var.public_ssh_key != "" ? "" : tls_private_key.ssh.public_key_openssh
}
