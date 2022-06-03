#!/bin/bash
cd external-docker-containers
for f in *.sh; do
  echo "Starting Execution : $f"
  bash $f
done
cd ..
cd docker-build-containers
for f in *.sh; do
  echo "Starting Execution : $f"
  bash $f
done