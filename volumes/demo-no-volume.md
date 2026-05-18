# Scenario 1: No Volume — Data Dies

Goal: prove that data written inside a container disappears when the container is deleted.

Run each command, watch each output. Don't skip — the "aha" is in the failure at step 6.

---

## Step 1 — Start Postgres with NO volume

```bash
docker run -d --name pg-novol \
  -e POSTGRES_PASSWORD=demo \
  -e POSTGRES_DB=demo \
  postgres:16-alpine
```

No `-v` flag. Data will live inside the container's writable layer.

## Step 2 — Wait until Postgres is ready

```bash
docker exec pg-novol pg_isready -U postgres
```

Repeat until you see `accepting connections`.

## Step 3 — Write data

```bash
docker exec pg-novol psql -U postgres -d demo -c \
  "CREATE TABLE notes(id serial, msg text); INSERT INTO notes(msg) VALUES ('hello from container 1'), ('this will vanish');"
```

## Step 4 — Read data back (proof it exists)

```bash
docker exec pg-novol psql -U postgres -d demo -c "SELECT * FROM notes;"
```

Expected: 2 rows.

## Step 5 — DESTROY the container

```bash
docker rm -f pg-novol
```

The writable layer is deleted with it.

## Step 6 — Recreate fresh container, same name

```bash
docker run -d --name pg-novol \
  -e POSTGRES_PASSWORD=demo \
  -e POSTGRES_DB=demo \
  postgres:16-alpine
```

Wait again:

```bash
docker exec pg-novol pg_isready -U postgres
```

## Step 7 — Try to read the data

```bash
docker exec pg-novol psql -U postgres -d demo -c "SELECT * FROM notes;"
```

**Expected error:**

```
ERROR:  relation "notes" does not exist
```

Table is gone. Database is gone. Everything is gone.

## Cleanup

```bash
docker rm -f pg-novol
```

---

## Lesson

Container filesystem is **ephemeral**. The writable layer dies with the container. No volume = no persistence.

Next: try [demo-with-volume.md](demo-with-volume.md) and see the opposite.
