#!/bin/bash
cd /app/user-order-service

docker stop user-order-service 2>/dev/null || true
docker rm user-order-service 2>/dev/null || true

docker build -t user-order-service .

docker run -d \
  --name user-order-service \
  --restart always \
  -p 8081:8081 \
  -e DB_URL="${DB_URL}" \
  -e DB_USERNAME="${DB_USERNAME}" \
  -e DB_PASSWORD="${DB_PASSWORD}" \
  -e JWT_SECRET="${JWT_SECRET}" \
  user-order-service