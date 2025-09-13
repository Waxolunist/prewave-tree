FROM ghcr.io/graalvm/graalvm-community:21 AS build-aot

WORKDIR /app

RUN curl -L -O https://www-eu.apache.org/dist/maven/maven-3/3.9.11/binaries/apache-maven-3.9.11-bin.tar.gz \
    && tar xf apache-maven-3.9.11-bin.tar.gz -C /opt \
    && ln -s /opt/apache-maven-3.9.11 /opt/maven
ENV M2_HOME=/opt/maven
ENV PATH=${M2_HOME}/bin:${PATH}
ENV MAVEN_OPTS='-Xmx8g -XX:MaxMetaspaceSize=2g'

COPY ./pom.xml ./pom.xml
COPY src ./src/
COPY .env ./.env

RUN mvn -Dmaven.test.skip=true -Djooq.codegen.skip=true -Dkover.skip=true -Pnative native:compile

FROM debian:stable-slim
LABEL maintainer="Christian Sterzl <christian.sterzl@gmail.com>"

WORKDIR /app

RUN apt-get update && apt-get -y upgrade && apt-get install -y curl

COPY --from=build-aot /app/target/supplychain /usr/bin/supplychain

HEALTHCHECK --interval=5s --timeout=3s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

CMD [ "/usr/bin/supplychain" ]