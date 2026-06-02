#!/usr/bin/env bash
docker-compose down
rm -rf .db
docker-compose up -d