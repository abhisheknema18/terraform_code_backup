output "id" {
  description = "returns a string"
  value       = helm_release.this.id
}

output "manifest" {
  description = "returns a string"
  value       = helm_release.this.manifest
}

output "metadata" {
  description = "returns a list of object"
  value       = helm_release.this.metadata
}

output "status" {
  description = "returns a string"
  value       = helm_release.this.status
}

output "version" {
  description = "returns a string"
  value       = helm_release.this.version
}

output "this" {
  value = helm_release.this
}