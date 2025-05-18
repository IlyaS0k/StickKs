FROM maven:3.9.3-eclipse-temurin-20-alpine as builder

WORKDIR /app

COPY . .

RUN mvn package -Dmaven.test.skip=true

FROM ubuntu:22.04

RUN apt-get update

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get -y --no-install-recommends install \
   wget unzip

RUN apt-get install -y make git zlib1g-dev libssl-dev gperf php-cli cmake default-jdk g++ openjdk-17-jre

WORKDIR /app

COPY --from=builder /app/target/ ./target/

COPY --from=builder /app/libs/libtdjni.so ./libs/libtdjni.so

RUN echo '#!/bin/sh' > /app/start.sh && \
    echo 'mv ./target/StickKs-0.0.1.jar ./StickKs.jar' >> /app/start.sh && \
    echo 'jar -xf ./StickKs.jar' >> /app/start.sh && \
    echo 'CLASS_PATH=$(find /app/BOOT-INF/lib -name "*.jar" | paste -sd:)' >> /app/start.sh && \
    echo 'java -cp "/app/target/classes:$CLASS_PATH" ru.ilyasok.StickKs.StickKsAppKt' >> /app/start.sh && \
    chmod +x /app/start.sh

ENTRYPOINT ["/app/start.sh"]
