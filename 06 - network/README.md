# Docker Networking Demo

How containers find — or don't find — each other.

## Walkthroughs

1. [demo-default-bridge.md](demo-default-bridge.md) — default `bridge`: no DNS, name lookup fails
2. [demo-user-network.md](demo-user-network.md) — user-defined network: names just work
3. [demo-isolation.md](demo-isolation.md) — separate networks isolate containers; connect a container to both to bridge them
4. [cleanup.md](cleanup.md) — wipe everything

Run in order. The "aha" moments:

- Scenario 1: `ping: bad address 'web'` on default bridge
- Scenario 2: same command works after switching to user-defined network
- Scenario 3: DNS fails across networks until you `docker network connect`

## Quick comparison

| Network type             | Auto DNS | Use case                                  |
|--------------------------|----------|-------------------------------------------|
| Default `bridge`         | ❌       | Legacy. Avoid.                            |
| User-defined `bridge`    | ✅       | Local multi-container apps (what compose uses) |
| `host`                   | n/a      | Container shares host's network stack (Linux only) |
| `none`                   | n/a      | Fully isolated container, no networking   |
| `overlay`                | ✅       | Multi-host (Swarm / k8s-like clustering)  |

## Useful commands

```bash
docker network ls                          # list all networks
docker network inspect <name>              # subnet, gateway, attached containers
docker network create <name>               # create user-defined bridge
docker network connect <net> <container>   # attach running container to network
docker network disconnect <net> <container>
docker network rm <name>                   # delete (must have no containers)
```

## How this maps to compose

Every `docker compose up` automatically:
1. Creates one user-defined bridge network named `<project>_default`
2. Attaches every service to it
3. Registers each service name in Docker's embedded DNS

That's why in your compose file you can write `jdbc:postgresql://postgres:5432/...` and it just works.
