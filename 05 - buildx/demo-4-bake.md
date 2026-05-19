# Scenario 4: docker buildx bake (declarative builds)

Goal: replace long shell scripts with a single config file. Build many images in parallel from one command.

Bake file in this folder: [`docker-bake.hcl`](docker-bake.hcl).

---

## Step 1 — Inspect the bake file

```bash
cat docker-bake.hcl
```

Three targets:
- `app` — single platform, tags `demo:local`
- `app-multi` — amd64 + arm64, output OCI tarball
- `app-cached` — local cache to `./buildcache`

Two groups: `default` (just `app`), `all` (both `app` + `app-multi`).

## Step 2 — Build the default target

```bash
docker buildx bake
```

Same as `docker buildx bake default` → builds `app`.

## Step 3 — Build a specific target

```bash
docker buildx bake app-multi
```

Multi-arch OCI tarball goes to `./demo-multi.tar`.

## Step 4 — Build a group (parallel)

```bash
docker buildx bake all
```

BuildKit runs both targets concurrently. Look at the progress output — interleaved layers.

## Step 5 — Override a variable

```bash
TAG=v2 docker buildx bake app
```

Builds tagged `v2:local`. Variables in `docker-bake.hcl` read env vars by name.

## Step 6 — Print the resolved config without building

```bash
docker buildx bake --print
```

Shows the JSON BuildKit will actually execute. Useful for debugging.

---

## Lesson

`bake` is to `docker build` what compose is to `docker run` — declarative, multi-target, parallel. Perfect for:
- Monorepos building many services
- CI pipelines (one bake call replaces N build steps)
- Matrix builds across platforms + variants

Supports HCL, JSON, and compose-file syntax.
