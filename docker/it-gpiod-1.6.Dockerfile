FROM eclipse-temurin:25-jdk-jammy

RUN apt-get update \
 && apt-get install -y --no-install-recommends \
      ca-certificates libgpiod-dev gpiod \
 && gpiodetect --version | grep '1\.6' \
 && ldconfig -p | grep libgpiod \
 && rm -rf /var/lib/apt/lists/*
