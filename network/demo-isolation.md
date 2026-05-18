# Scenario 3: Network Isolation — Different Networks Can't Talk

Goal: show that containers on **separate** networks are isolated. Two networks = two private rooms.

---

## Step 1 — Create two separate networks

```bash
docker network create frontend-net
docker network create backend-net
```

## Step 2 — Start a service on each network

```bash
docker run -d --name api --network backend-net nginx:alpine
docker run -d --name browser --network frontend-net alpine sleep infinity
```

`browser` is on `frontend-net`. `api` is on `backend-net`. No overlap.

## Step 3 — Try to reach `api` from `browser`

```bash
docker exec browser ping -c 2 api
```

**Expected error:**

```
ping: bad address 'api'
```

DNS only resolves names within the same network. Total isolation.

## Step 4 — Connect `browser` to the backend network too

```bash
docker network connect backend-net browser
```

A container can be attached to multiple networks. Now `browser` lives in both.

## Step 5 — Ping again

```bash
docker exec browser ping -c 2 api
```

**Now it works.** Once they share a network, DNS resolves.

## Step 6 — Inspect to confirm

```bash
docker inspect -f '{{range $k, $v := .NetworkSettings.Networks}}{{$k}} {{end}}' browser
```

Output: `backend-net frontend-net`

## Cleanup

```bash
docker rm -f api browser
docker network rm frontend-net backend-net
```

---

## Lesson

Networks are **security boundaries**. Put your database on `backend-net`, your public API on both `frontend-net` + `backend-net`, your web tier on `frontend-net` only. The DB is unreachable from the public side — by design, not by firewall rules. This is the standard pattern in compose:

```yaml
services:
  web:
    networks: [frontend, backend]
  db:
    networks: [backend]
networks:
  frontend:
  backend:
```
