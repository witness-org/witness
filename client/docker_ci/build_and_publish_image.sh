#!/usr/bin/env bash

# docker login (not required if already logged in)

docker build -t raffaelfoidl/flutter-integration-test:latest .
docker push raffaelfoidl/flutter-integration-test:latest