# Scenario 2: Multi-Arch Build (amd64 + arm64 in one command)

Goal: build a single image that runs natively on Intel/AMD **and** Apple Silicon / Raspberry Pi 4 / AWS Graviton.

Requires the `demo-builder` from scenario 1, or any `docker-container` driver builder.

---

## Step 1 — Make sure you're on a multi-arch builder

```bash
docker buildx use demo-builder
docker buildx inspect
```

Look for `Platforms:` line listing `linux/amd64, linux/arm64, ...`.

## Step 2 — Build for one platform (the native one)

```bash
cd app
docker buildx build --platform linux/amd64 -t demo:amd64 --load .
```

`--load` imports the image into your local `docker images`. Single-platform only.

```bash
docker run --rm demo:amd64
```

Output shows it was built for `linux/amd64`.

## Step 3 — Build for TWO platforms in one shot

```bash
docker buildx build \
  --platform linux/amd64,linux/arm64 \
  -t demo:multi \
  .
```

No `--load`. Multi-arch images can't be loaded into the legacy local store. They must be pushed (`--push`) to a registry, or output to OCI tarball.

Output shows BuildKit running **both** builds in parallel.

## Step 4 — Build and push to a registry

If you have Docker Hub / GHCR access:

```bash
docker buildx build \
  --platform linux/amd64,linux/arm64,linux/arm/v7 \
  -t YOUR_USER/demo:multi \
  --push \
  .
```

Then anyone on any of those architectures can `docker pull YOUR_USER/demo:multi` and get the correct image.

## Step 5 — Output to OCI tarball (no registry needed)

```bash
docker buildx build \
  --platform linux/amd64,linux/arm64 \
  -t demo:multi \
  --output type=oci,dest=./demo-multi.tar \
  .
```

You'll see `demo-multi.tar` on disk — the multi-arch image as a portable file.

## Step 6 — Inspect the manifest

If you pushed it:

```bash
docker buildx imagetools inspect YOUR_USER/demo:multi
```

Output shows multiple `Manifest` blocks, one per platform.

---

## Lesson

One command, many architectures, parallel builds. No more "works on my Mac, broken on the server". The manifest list lets the registry serve the right binary to each puller automatically.
