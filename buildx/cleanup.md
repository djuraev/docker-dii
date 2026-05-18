# Cleanup

```bash
# Remove built images
docker rmi demo:amd64 demo:multi demo:cache demo:local demo:cached 2>/dev/null

# Switch back to default builder
docker buildx use default

# Remove the demo builder
docker buildx rm demo-builder

# Wipe local cache artifacts
rm -rf ./buildcache ./demo-multi.tar
```

Errors for non-existent items are safe to ignore.
