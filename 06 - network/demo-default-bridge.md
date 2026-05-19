# Scenario 1: Default Bridge — No DNS Between Containers

Goal: show that containers on Docker's **default** `bridge` network cannot reach each other by name.

---

## Step 1 — Start two containers on the default network

```bash
docker run -d --name web nginx:alpine
docker run -d --name client alpine sleep infinity
```

Both attached to the built-in `bridge` network (no `--network` flag).

## Step 2 — Try to ping `web` by name from `client`

```bash
docker exec client ping -c 2 web
```

**Expected error:**

```
ping: bad address 'web'
```

Default `bridge` does NOT provide automatic DNS between containers.

## Step 3 — Find `web`'s IP manually

```bash
docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' web
```

Example output: `172.17.0.2`

## Step 4 — Ping by IP (works, but ugly)

```bash
docker exec client ping -c 2 172.17.0.2
```

Now it works — but hardcoding IPs is brittle. IPs change every restart.

## Cleanup

```bash
docker rm -f web client
```

---

## Lesson

The default `bridge` is legacy. No DNS. Don't use it for multi-container apps. Always create a **user-defined network** — see scenario 2.
