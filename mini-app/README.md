# Build a Mini Startup

Spring Boot REST API + Postgres + Redis. One command.

## Boot the stack

```bash
docker compose up --build
```

API on `http://localhost:8080`.

## Endpoints

```bash
# Increments Redis counter, reads Postgres count
curl http://localhost:8080/

# Insert into Postgres
curl -X POST "http://localhost:8080/visits?note=hello"

# Read from Postgres
curl http://localhost:8080/visits
```

## Service discovery by name

The compose network gives each service a DNS name = its compose service key. Code never hardcodes IPs.

In `application.yml`:

```yaml
url: jdbc:postgresql://postgres:5432/miniapp   # "postgres" = service name
host: redis                                     # "redis" = service name
```

Prove it live — exec into the api container, resolve the names:

```bash
docker compose exec api sh -c "getent hosts postgres; getent hosts redis"
```

Output shows each name pointing to internal compose-network IP (172.x.x.x).

Hit Redis from the API container:

```bash
docker compose exec api sh -c "apk add --no-cache redis >/dev/null && redis-cli -h redis ping"
# PONG
```

Hit Postgres:

```bash
docker compose exec postgres psql -U miniapp -d miniapp -c "SELECT count(*) FROM visit;"
```

## Teardown

```bash
docker compose down          # stop, keep volume
docker compose down -v       # stop + wipe pgdata
```

## Why this works

| Old way                      | Compose way                       |
|------------------------------|-----------------------------------|
| Hardcode `localhost:5432`    | Use service name `postgres`       |
| Manage IPs / `/etc/hosts`    | Docker's embedded DNS does it     |
| Start each container by hand | `docker compose up` boots all 3   |
| Race conditions on startup   | `depends_on` + healthchecks gate the API |
