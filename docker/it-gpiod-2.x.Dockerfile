FROM eclipse-temurin:25-jdk

RUN apt-get update \
 && apt-get install -y --no-install-recommends \
      ca-certificates libgpiod-dev gpiod \
 && gpiodetect --version | grep -E '2\.' \
 && ldconfig -p | grep libgpiod \
 && rm -rf /var/lib/apt/lists/*
