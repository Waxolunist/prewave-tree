# How To Start

## Prerequisits

### Required

- JDK21
- Docker
- Docker compose

### Optional

- GraalVM JDK21

## Preparation

Copy the .env.template to .env

    cp env.template .env

Change sensitive settings if you want, such as usernames or passwords.

## Running the application with docker

To run the application start the containers

    docker compose up -d

## Running the application on the host

To run the application start the database

    docker compose up -d postgres

Then you can start the application

    mvn spring-boot:run

## Start Development

Build the application with 

    mvn clean install

This builds the application and runs the tests. If the tests fail, see chapter [Troubleshooting](#troubleshooting).

## Generating the JOOQ Code

By default the JOOQ code generation is disabled. To enable it, enable the jooq profile.

    mvn -Pjooq clean install

## Building the GraalVM image and GrallVM metadata

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

### Controller design

The controller contains 2 different approaches to the response schema.
One is a standard approach, which is in trees with over a million edges 
slightly faster, the other is a streaming approch, which is faster in 
the time to the first byte.

You can measure the times with these commands:

    $ curl -o /dev/null -s -w 'Establish Connection: %{time_connect}s\nTTFB: %{time_starttransfer}s\nTotal: %{time_total}s\n' --location 'http://localhost:8080?from=800000'       

    $ curl -o /dev/null -s -w 'Establish Connection: %{time_connect}s\nTTFB: %{time_starttransfer}s\nTotal: %{time_total}s\n' --location 'http://localhost:8080/stream?from=800000'

### Response design decisions

The response format is a list of Nodes. Each entry then contains a list of targets.

```
[
    {
        "from": 8000135,
        "to": [
            8000136,
            8000137,
            8000138,
            8000139,
            8000140,
            8000141,
            8000142,
            8000143,
            8000144,
            8000145
        ]
    },
    [...]
]
```

### Performance

The performance is also with very large trees considerably good. The recursiveness of
fetching trees is handled by the database, thus pretty fast.
To test the performance, populate the database with the `scripts/loadtest.sql`.
This generates 100 trees with 99.900 nodes, each node having 10 connections.

Then try the loadtest feature in postman with the "GetTree Random" script.
The results on my laptop were pretty good, having steady response times.
The test was made over the course of 10 minutes with 10 concurrent users.
The average response time was around 12 seconds, the streaming method was
slightly slower. 
Smaller trees resulted in much better response times of course.
Single requests are of course faster.

The memory usage of the application was stable between 200MB and 300MB. The memory 
usage of the application idling is about 150MB. The streaming method used
constantly less memory during the performance test between 80MB and 120MB.

The performance could be increased mainly by tuning the database, e.g. splitting
the edge table into partitions or assigning more cpus to it.

![Postman Performance run 1](doc/postman_gettree.png?raw=true)
![Postman Performance run 2](doc/postman_gettreestream.png?raw=true)

The postman collection for testing is contained in the folder `doc`.

## References

### REST responses

I chose the Response Codes according to this book:

[RESTful Web Services Cookbook](https://books.google.at/books?id=LDuzpQlVuG4C&printsec=frontcover&hl=de#v=onepage&q&f=false)

### Streaming JSON Output

On how to stream json output I read [Blogentry](https://alexanderobregon.substack.com/p/streaming-json-output-in-spring-boot)

### Building the graalvm image

- [GraalVM Getting started](https://www.graalvm.org/jdk21/docs/getting-started/)
- [Minimal docker images](https://aws.amazon.com/blogs/opensource/using-graalvm-build-minimal-docker-images-java-applications/)