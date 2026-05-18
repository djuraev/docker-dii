# Scenario 3: Cache Mounts and Remote Cache

Goal: see how BuildKit caches make rebuilds blazing fast — and how to share cache across machines/CI.

---

## Step 1 — First build (cold)

```bash
cd app
time docker buildx build -t demo:cache --load .
```

Note the time. BuildKit downloads alpine, runs `apk add`, etc.

## Step 2 — Second build (warm)

Without changing anything:

```bash
time docker buildx build -t demo:cache --load .
```

Near-instant. Every layer says `CACHED`.

## Step 3 — Change one line, see partial cache

Edit `app/Dockerfile`, change `/info.txt` content:

```dockerfile
RUN echo "rebuilt" > /info.txt
```

Then:

```bash
time docker buildx build -t demo:cache --load .
```

Earlier layers (FROM, apk add) → `CACHED`. Only the changed `RUN` re-runs.

## Step 4 — Add a cache mount for package managers

Edit `app/Dockerfile`:

```dockerfile
FROM alpine:3.20
RUN --mount=type=cache,target=/var/cache/apk \
    apk add --no-cache bash curl jq
```

`--mount=type=cache` keeps a directory across builds **without** baking it into a layer. Apt/pip/npm/maven all benefit.

```bash
docker buildx build -t demo:cache --load .
```

Re-run after editing — even on a "clean" build, `/var/cache/apk` is reused.

## Step 5 — Export cache to a local directory

```bash
docker buildx build \
  -t demo:cache \
  --cache-to type=local,dest=./buildcache,mode=max \
  --cache-from type=local,src=./buildcache \
  --load \
  .
```

Look at `./buildcache/` — BuildKit's cache as files. You can `rsync` this to another machine and reuse it.

## Step 6 — Use registry as cache (CI superpower)

```bash
docker buildx build \
  -t YOUR_USER/demo:latest \
  --cache-to type=registry,ref=YOUR_USER/demo:buildcache,mode=max \
  --cache-from type=registry,ref=YOUR_USER/demo:buildcache \
  --push \
  .
```

Every CI runner pulls the cache from the registry. Cold runners build as fast as warm laptops.

---

## Lesson

Three cache levels:
1. **Local layer cache** — automatic, per-builder
2. **Cache mounts** (`--mount=type=cache`) — keep package caches out of the image but persistent
3. **External cache** (`--cache-to/from`) — share between machines, CI jobs, teams
