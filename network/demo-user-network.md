# Scenario 2: User-Defined Network — DNS By Container Name

Goal: show that containers on a user-defined bridge can reach each other by name. No IPs.

---

## Step 1 — Create a user-defined network

```bash
docker network create app-net
```

Verify:

```bash
docker network ls
```

## Step 2 — Start two containers ON that network

```bash
docker run -d --name web --network app-net nginx:alpine
docker run -d --name client --network app-net alpine sleep infinity
```

## Step 3 — Ping `web` by name from `client`

```bash
docker exec client ping -c 2 web
```

**Expected:**

```
PING web (172.18.0.2): 56 data bytes
64 bytes from 172.18.0.2: seq=0 ttl=64 time=0.1 ms
```

Works. Docker's embedded DNS resolves `web` → container IP.

## Step 4 — Hit nginx with curl by name

```bash
docker exec client wget -qO- http://web
```

You get nginx's welcome HTML. The hostname `web` is the service.

## Step 5 — Inspect the network

```bash
docker network inspect app-net
```

See both containers under `Containers`, each with an IP from the subnet.

## Step 6 — Stop and recreate `web`, then ping again

```bash
docker rm -f web
docker run -d --name web --network app-net nginx:alpine
docker exec client ping -c 2 web
```

New container, possibly new IP — name still resolves. DNS handles the change.

## Cleanup

```bash
docker rm -f web client
docker network rm app-net
```

---

## Lesson

User-defined networks give you free service discovery: containers find each other by **name**, not IP. This is exactly how compose works under the hood — every `compose up` creates one user-defined network and puts all services on it.
