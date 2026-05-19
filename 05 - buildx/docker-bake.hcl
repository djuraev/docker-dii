# Bake file: declarative builds, parallel, groupable.
# Run with: docker buildx bake <target>

group "default" {
  targets = ["app"]
}

group "all" {
  targets = ["app", "app-multi"]
}

variable "TAG" {
  default = "demo"
}

target "app" {
  context    = "./app"
  dockerfile = "Dockerfile"
  tags       = ["${TAG}:local"]
  platforms  = ["linux/amd64"]
}

target "app-multi" {
  inherits  = ["app"]
  tags      = ["${TAG}:multi"]
  platforms = ["linux/amd64", "linux/arm64"]
  output    = ["type=oci,dest=./demo-multi.tar"]
}

target "app-cached" {
  inherits   = ["app"]
  tags       = ["${TAG}:cached"]
  cache-to   = ["type=local,dest=./buildcache,mode=max"]
  cache-from = ["type=local,src=./buildcache"]
}
