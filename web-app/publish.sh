#!/usr/bin/env bash
# Publish web-app image to Docker Hub
# Usage: ./publish.sh <dockerhub-username> [tag]
# Example: ./publish.sh mamurdjuraev v1.0

set -euo pipefail

USERNAME="${1:?Docker Hub username required. Usage: ./publish.sh <username> [tag]}"
TAG="${2:-latest}"
IMAGE_NAME="tictactoe-web"
FULL_IMAGE="${USERNAME}/${IMAGE_NAME}:${TAG}"

echo "==> Building image: ${FULL_IMAGE}"
docker build -t "${FULL_IMAGE}" -t "${USERNAME}/${IMAGE_NAME}:latest" .

echo "==> Logging in to Docker Hub"
docker login -u "${USERNAME}"

echo "==> Pushing ${FULL_IMAGE}"
docker push "${FULL_IMAGE}"

if [ "${TAG}" != "latest" ]; then
  echo "==> Pushing ${USERNAME}/${IMAGE_NAME}:latest"
  docker push "${USERNAME}/${IMAGE_NAME}:latest"
fi

echo "==> Done. Pull with:"
echo "    docker pull ${FULL_IMAGE}"
echo "==> Run with:"
echo "    docker run -d -p 8080:8080 --name tictactoe ${FULL_IMAGE}"
