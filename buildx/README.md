# Docker Buildx Demo

Buildx = the modern Docker build CLI, powered by BuildKit. Multi-arch, parallel, cached, declarative.

## Walkthroughs

1. [demo-1-builders.md](demo-1-builders.md) — what a builder is, create + switch one
2. [demo-2-multiarch.md](demo-2-multiarch.md) — one command, amd64 + arm64 + arm/v7
3. [demo-3-cache.md](demo-3-cache.md) — layer cache, cache mounts, registry cache
4. [demo-4-bake.md](demo-4-bake.md) — declarative builds via `docker-bake.hcl`
5. [cleanup.md](cleanup.md) — wipe everything

Run in order. Each builds on the previous.

## Files

- `app/Dockerfile` — tiny alpine image that prints its build platform
- `docker-bake.hcl` — declarative build config for scenario 4

## Buildx vs legacy `docker build`

| Feature                          | Legacy build | Buildx |
|----------------------------------|--------------|--------|
| Multi-arch images                | ❌           | ✅     |
| Parallel stage execution         | ❌           | ✅     |
| `--mount=type=cache`             | ❌           | ✅     |
| `--mount=type=secret` / `=ssh`   | ❌           | ✅     |
| External cache (`--cache-to/from`) | ❌         | ✅     |
| Build via remote machine / k8s   | ❌           | ✅     |
| Declarative (`bake`)             | ❌           | ✅     |

Buildx is the default in Docker Desktop. Use it.

## Cheat sheet

```bash
# Builders
docker buildx ls
docker buildx create --name X --driver docker-container --bootstrap
docker buildx use X

# Build
docker buildx build --platform linux/amd64,linux/arm64 -t img --push .

# Cache
docker buildx build --cache-to type=registry,ref=REPO:cache,mode=max \
                    --cache-from type=registry,ref=REPO:cache ...

# Inspect
docker buildx imagetools inspect REPO:tag

# Bake
docker buildx bake [target|group]
docker buildx bake --print
```
