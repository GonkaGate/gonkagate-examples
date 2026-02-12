# syntax=docker/dockerfile:1.7

FROM --platform=$BUILDPLATFORM golang:1.22-alpine AS builder
WORKDIR /src

COPY go.mod go.sum ./
RUN go mod download

COPY . .

ARG TARGETOS
ARG TARGETARCH
RUN CGO_ENABLED=0 GOOS=$TARGETOS GOARCH=$TARGETARCH go build -trimpath -ldflags="-s -w" -o /out/gonkagate-chat .

FROM alpine:3.20 AS runner
RUN apk add --no-cache ca-certificates
RUN adduser -D -u 10001 appuser

WORKDIR /home/appuser
COPY --from=builder /out/gonkagate-chat /usr/local/bin/gonkagate-chat

USER appuser

ENTRYPOINT ["gonkagate-chat"]
CMD ["chat", "--stream=true"]
