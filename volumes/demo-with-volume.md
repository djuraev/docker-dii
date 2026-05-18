# Scenario 2: Named Volume — Data Survives

Same flow as scenario 1, but with one extra flag: `-v pgdata-demo:/var/lib/postgresql/data`. Watch the data survive container destruction.

---

## Step 1 — Start Postgres WITH a named volume

```bash
docker run -d --name pg-vol \
  -e POSTGRES_PASSWORD=demo \
  -e POSTGRES_DB=demo \
  -v pgdata-demo:/var/lib/postgresql/data \
  postgres:16-alpine
```

`pgdata-demo` is the volume name. Docker creates it automatically if it doesn't exist.

## Step 2 — Wait for Postgres

```bash
docker exec pg-vol pg_isready -U postgres
```

Repeat until you see `accepting connections`.

## Step 3 — Write data

```bash
docker exec pg-vol psql -U postgres -d demo -c \
  "CREATE TABLE notes(id serial, msg text); INSERT INTO notes(msg) VALUES ('hello from container 1'), ('this should SURVIVE');"
```

## Step 4 — Read it back

```bash
docker exec pg-vol psql -U postgres -d demo -c "SELECT * FROM notes;"
```

Expected: 2 rows.

## Step 5 — DESTROY the container

```bash
docker rm -f pg-vol
```

## Step 6 — Confirm the volume still exists on the host

```bash
docker volume ls | grep pgdata-demo
```

Output:

```
local     pgdata-demo
```

Container dead. Volume alive.

## Step 7 — Recreate fresh container, mount the SAME volume

```bash
docker run -d --name pg-vol \
  -e POSTGRES_PASSWORD=demo \
  -e POSTGRES_DB=demo \
  -v pgdata-demo:/var/lib/postgresql/data \
  postgres:16-alpine
```

Wait:

```bash
docker exec pg-vol pg_isready -U postgres
```

## Step 8 — Read the data again

```bash
docker exec pg-vol psql -U postgres -d demo -c "SELECT * FROM notes;"
```

**Both rows are still there.** No `CREATE TABLE` was run on the second container. The volume already had the data.

## Step 9 — Inspect the volume

```bash
docker volume inspect pgdata-demo
```

Look at `Mountpoint` — that's where Docker stores volume data on the host (inside the Docker VM on Mac/Windows).

## Cleanup

```bash
docker rm -f pg-vol
docker volume rm pgdata-demo
```

---

## Lesson

Named volumes live **outside the container lifecycle**. Delete container — volume stays. Mount volume on a new container — data is right where you left it.

This is how every production database container survives upgrades, restarts, and rebuilds.
