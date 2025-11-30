provider "kubernetes" {
  # Use in-cluster config when running from a pod
  # The delegate's service account will be used for authentication
}

resource "kubernetes_namespace" "ns" {
  metadata {
    name = "${var.namespace-name}"
  }
}

output "namespace" {
  value = kubernetes_namespace.ns.metadata[0].name
}

variable "namespace-name" {
  type = string
}