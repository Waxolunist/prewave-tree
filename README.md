# How To Start

## Prerequisits

- JDK21

## Start

Copy the .env.template to .env

    cp env.template .env

You change in this file sensitive settings, such as usernames or passwords.

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

I decided early on to go with GraalVM for the runtime, because I heard good things about
it regarding performance, and when taking on a task like this, I like
to add one new thing, I want to learn.

After generating the code and successfully executing the generated hello world I wrote the user stories
in github issues and prioritized them.

# Architectural considerations

JOOQ is a very performant and well known database access library. Postgres is also very performant for that
task. I know both very well, so the task was predestined for recursive queries within postgres which should
result in a very thin domain layer.
The hardest part was to avoid infinite queries because of possible cycles.