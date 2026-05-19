# Volume Persistence Demo

Same Postgres image, two runs. One forgets. One remembers.

## Walkthroughs

1. [demo-no-volume.md](demo-no-volume.md) — write data, destroy container, recreate → **data gone**
2. [demo-with-volume.md](demo-with-volume.md) — same flow with a named volume → **data survives**
3. [cleanup.md](cleanup.md) — wipe everything

Run them in order. The "aha" is the failing `SELECT` at the end of scenario 1 followed by the same `SELECT` succeeding in scenario 2.

## Side-by-side summary

| Action                       | No volume          | Named volume        |
|------------------------------|--------------------|---------------------|
| Write data                   | OK                 | OK                  |
| Stop container               | Data still on disk | Data still on disk  |
| **Delete container**         | **Data gone**      | **Data still there**|
| Recreate container, read     | Empty DB           | Rows present        |

## Bonus: bind mount

Mount a host directory instead of a named volume:

```bash
mkdir -p ./pgdata-bind
docker run -d --name pg-bind \
  -e POSTGRES_PASSWORD=demo \
  -v "$(pwd)/pgdata-bind:/var/lib/postgresql/data" \
  postgres:16-alpine
```

Look at `./pgdata-bind/` on the host — Postgres files appear. Same persistence, but a path you control instead of one Docker manages.

Cleanup:

```bash
docker rm -f pg-bind
rm -rf ./pgdata-bind
```
