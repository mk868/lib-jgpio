#!/usr/bin/env bash
set -euo pipefail

echo "=== Building lib-jgpio-it:gpiod-1.6 ==="
docker build -f docker/it-gpiod-1.6.Dockerfile -t lib-jgpio-it:gpiod-1.6 .

echo "=== Building lib-jgpio-it:gpiod-2.x ==="
docker build -f docker/it-gpiod-2.x.Dockerfile -t lib-jgpio-it:gpiod-2.x .

echo "=== Building lib-jgpio-it:no-gpiod ==="
docker build -f docker/it-no-gpiod.Dockerfile -t lib-jgpio-it:no-gpiod .
