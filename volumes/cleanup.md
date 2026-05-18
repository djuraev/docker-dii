# Cleanup

Nuke all demo containers + volume.

```bash
docker rm -f pg-novol pg-vol
docker volume rm pgdata-demo
```

If a name doesn't exist Docker prints an error — safe to ignore.
