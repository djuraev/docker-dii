# Cleanup

Nuke all demo containers + networks.

```bash
docker rm -f web client api browser
docker network rm app-net frontend-net backend-net
```

If a name doesn't exist Docker prints an error — safe to ignore.
