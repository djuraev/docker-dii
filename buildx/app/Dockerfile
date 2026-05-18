# Tiny demo image. Prints architecture at runtime so multi-arch builds are visible.

FROM alpine:3.20

RUN apk add --no-cache bash

# TARGETARCH is automatically set by buildx during multi-arch builds.
ARG TARGETARCH
ARG TARGETOS
ARG TARGETPLATFORM

RUN echo "Built for: $TARGETPLATFORM (os=$TARGETOS arch=$TARGETARCH)" > /info.txt

CMD ["sh", "-c", "cat /info.txt && uname -m"]
