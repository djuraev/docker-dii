# Multi-Stage Build Race

Demo: same Java app, two Dockerfiles. Watch image size drop.

## App

Trivial Java 17 + Maven app. Prints a line. Source in `app/`.

## Build both images

```bash
# Naive: ships JDK + Maven + source + .m2 cache
docker build -f Dockerfile.naive -t demo:naive .

# Multi-stage: ships JRE + jar only
docker build -f Dockerfile.multistage -t demo:multistage .
```

## Compare sizes live

```bash
docker images | grep demo
```

Expected:

```
demo   multistage   ...   ~200 MB
demo   naive        ...   ~1.2 GB
```

## Run

```bash
docker run --rm demo:naive
docker run --rm demo:multistage
```

Both print same output. Final size differs ~6x.

## Why the gap

| Layer                  | Naive | Multi-stage |
|------------------------|-------|-------------|
| Base OS                | Ubuntu (~80 MB) | Alpine (~5 MB) |
| JDK                    | ✅ full | ❌ JRE only |
| Maven binary           | ✅ | ❌ |
| `.m2` dependency cache | ✅ leaked in | ❌ |
| Source code            | ✅ leaked in | ❌ |
| `target/` build output | ✅ full | only jar |

Multi-stage `COPY --from=build` cherry-picks only the jar. Build tools stay in stage 1, discarded.

## Bonus drill

Inspect layers:

```bash
docker history demo:naive
docker history demo:multistage
```

Show students which layers bloat the naive image.
