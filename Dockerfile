FROM 	ubuntu:20.04

RUN \
 	apt-get update && \
 	apt-get install -y openjdk-21-jdk && \
 	apt-get install -y maven && \
    apt-get install -y default-jdk && \
    apt-get install -y cmake && \
    apt-get install -y make && \
    apt-get install -y git && \
    apt-get install -y zlib1g-dev && \
    apt-get install -y libssl-dev && \
    apt-get install -y gperf && \
    apt-get install -y php-cli && \
    apt-get install -y g++

VOLUME /root/.m2/repository

ARG APP_DIR

ENV STICKS_DIR=$APP_DIR

WORKDIR $STICKS_DIR

COPY 	. .

RUN /$STICKS_DIR/tdlib-install.sh

ENTRYPOINT	["mvn", "spring-boot:run"]
