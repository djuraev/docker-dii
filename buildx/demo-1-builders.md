# Scenario 1: Buildx Builders

Goal: see what a builder is, create one, switch to it.

`docker build` (legacy) uses the local daemon. `docker buildx` uses **builders** (BuildKit instances) — local containers, remote machines, or k8s pods.

---

## Step 1 — Check buildx is available

```bash
docker buildx version
```

Modern Docker Desktop ships it built in.

## Step 2 — List existing builders

```bash
docker buildx ls
```

You'll see `default` (legacy classic builder) and `desktop-linux` (BuildKit-powered).

## Step 3 — Create a new builder

```bash
docker buildx create --name demo-builder --driver docker-container --bootstrap
```

`--driver docker-container` runs BuildKit inside a Docker container. Required for multi-arch, advanced cache, etc.

`--bootstrap` boots it immediately.

## Step 4 — Inspect it

```bash
docker buildx inspect demo-builder
```

See the BuildKit version and supported platforms (e.g. `linux/amd64, linux/arm64, linux/arm/v7, ...`).

## Step 5 — Switch to it

```bash
docker buildx use demo-builder
docker buildx ls
```

The `*` marks the active builder. All `docker buildx build` calls will now go through it.

## Cleanup (skip if continuing to scenario 2)

```bash
docker buildx use default
docker buildx rm demo-builder
```

---

## Lesson

A builder is the **engine** doing the build. You can have many — local containers, remote SSH hosts, k8s clusters. Buildx talks to whichever is active.
