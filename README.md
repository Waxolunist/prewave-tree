# How To Start

## Prerequisits

### Required

- JDK21
- Docker
- Docker compose

### Optional

- GraalVM JDK21

## Running the application

Copy the .env.template to .env

    cp env.template .env

To run the application start the containers

    docker compose up -d

## Start Development

Copy the .env.template to .env (if you haven't done so already)

    cp env.template .env

Change sensitive settings if you want, such as usernames or passwords.

Build the application with 

    mvn clean install

This builds the application and runs the tests. If the tests fail, see chapter [Troubleshooting](#troubleshooting).

## Building the GraalVM image

1. First compile the spring image with the native profile:

```
mvn -Pnative spring-boot:build-image
```

2. Then run the application with graalvm and let the agent generate
the hint files.

```
/Library/Java/JavaVirtualMachines/graalvm-21.jdk/Contents/Home/bin/java -Dspring.aot.enabled=true \
        -agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image \
        -jar target/supplychain-0.0.1-SNAPSHOT.jar
```

3. Test the application.

4. Compile it to a graalvm native image

```
mvn -Dmaven.test.skip=true -Djooq.codegen.skip=true -Dkover.skip=true -Pnative native:compile
```

## Troubleshooting

If you run docker on colima on Mac OSX and apple silicon, please be aware that testcontainers
may not work with virtiofs, thus you have to change to sshfs.
Also you should have enough memory allocated to your docker host for the graalvm
image compilation.
I recommend at least 12G memory.

    colima start --edit

If the containers are still not running you may have to set some environment variables.

    export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock
    export TESTCONTAINERS_HOST_OVERRIDE=$(colima ls -j | jq -r '.address')
    export DOCKER_HOST="unix://${HOME}/.colima/default/docker.sock"

# My Approach

I started with analyzing the task at hand and enumerated the most important
architectural decisions, which were already in place.

These were:

- Spring Boot
- Kotlin
- Postgres
- REST Service
- JOOQ

After that I created an empty github repository with a generated
.gitignore for kotlin projects and scaffolded a project with spring
initializr.

I decided to go with Maven as build tool, because I am more familiar
with it.
Furthermore I added the following dependencies:

- GraalVM Native Support
- Spring Boot DevTools
- Docker Compose Support
- Spring Web
- JOOQ Access Layer
- PostgreSQL Driver
- Spring Boot Actuator 
- Testcontainers
- Liquibase

I decided early on to go with GraalVM native images for the runtime, because I heard good things about
it regarding performance, and when taking on a task like this, I like
to add one new thing, I want to learn.

After generating the code and successfully executing the generated hello world I wrote the user stories
in github issues and prioritized them.

## Architectural considerations

### Database and Business Layer

JOOQ is a very performant and well known database access library. Postgres is also very performant for that
task. I know both very well, so the task was predestined for recursive queries within postgres which should
result in a very thin domain layer and high performance.

The generated JOOQ files are versioned alongside with the source code,
because of runtime conflicts. This results in a conflict. The application has to run 
in order to generate the tables in the database, but the application can't compile without 
a running and populated database.

In the long run, I would separate the liquibase task from the application and also 
not generate the JOOQ code with the default build. This will accelerate build times
on the one hand and also increases security by removing parts of the application not 
needed during runtime.

### Testing and Linting

TODO 

The hardest part I thought will be to avoid infinite queries because of possible cycles.

### Response and request formats

TODO swagger docs

## References

### REST responses

I chose the Response Codes according to this book:

[RESTful Web Services Cookbook](https://books.google.at/books?id=LDuzpQlVuG4C&printsec=frontcover&hl=de#v=onepage&q&f=false)

### Streaming JSON Output

On how to stream json output I read [Blogentry](https://alexanderobregon.substack.com/p/streaming-json-output-in-spring-boot)

### Building the graalvm image

- [GraalVM Getting started](https://www.graalvm.org/jdk21/docs/getting-started/)
- [Minimal docker images](https://aws.amazon.com/blogs/opensource/using-graalvm-build-minimal-docker-images-java-applications/)