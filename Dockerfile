FROM 	ubuntu:22.04

RUN \
 	apt-get update && \
 	apt-get install -y openjdk-21-jdk && \
 	apt-get install -y maven 
 	
VOLUME /root/.m2/repository	

WORKDIR ${APP_DIR}

VOLUME /root/.m2/repository

COPY 	. .

ENTRYPOINT	["mvn","-Djava.library.path=${JAVA_LIBRARY_PATH}", "spring-boot:run"]
