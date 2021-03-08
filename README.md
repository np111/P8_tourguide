# [![TourGuide](.readme/logo.png?raw=true)](https://github.com/np111/P8_tourguide)
[![build](https://github.com/np111/P8_tourguide/actions/workflows/build.yml/badge.svg)](https://github.com/np111/P8_tourguide/actions/workflows/build.yml) [![codecov.io](https://codecov.io/github/np111/P8_tourguide/coverage.svg?branch=master)](https://codecov.io/github/np111/P8_tourguide?branch=master)

TourGuide is a mobile and PC application that will change the way you travel!

![Poster](.readme/poster.jpg?raw=true)

## Documentation

- [HTTP API Documentation](https://np111.github.io/P8_tourguide/index.html)

## Enhanced performance (730x faster!)

Since the legacy version, tracking performance was improved by parallelizing
tasks and replicating overloaded services (to remove the bottlenecks).

Here is a benchmark for 100 000 users (AMD Ryzen 9 3900XT - 24 x 4.0GHz • 64GB
RAM):

- Before **(2 hours, 5 minutes and 48 seconds)**:
  ```
  Begin Tracker. Tracking 100000 users.
  [...]
  Tracker Time Elapsed: 7548.387s.
  ```

- After **(10 seconds)**:
  ```
  Begin Tracker. Tracking 100000 users.
  Tracking progression:   7984/100000 Δ  7984 | 1.000s
  Tracking progression:  17514/100000 Δ  9530 | 2.000s
  Tracking progression:  27282/100000 Δ  9768 | 3.000s
  Tracking progression:  37610/100000 Δ 10328 | 4.000s
  Tracking progression:  47746/100000 Δ 10136 | 5.000s
  Tracking progression:  57736/100000 Δ  9990 | 6.000s
  Tracking progression:  67714/100000 Δ  9978 | 7.000s
  Tracking progression:  77620/100000 Δ  9906 | 8.000s
  Tracking progression:  87556/100000 Δ  9936 | 9.000s
  Tracking progression:  97174/100000 Δ  9618 | 10.000s
  Tracking progression: 100000/100000 Δ  2826 | 10.327s
  Tracker Time Elapsed: 10.327s.
  ```

## Getting started

These instructions will get you a copy of the project up and running on your
local machine for development.

### Prerequisites

- Install
  [Java 11+](https://adoptopenjdk.net/?variant=openjdk15&jvmVariant=hotspot)
- Install [Docker](https://docs.docker.com/get-docker/)
  and [Docker Compose](https://docs.docker.com/compose/install/)

### Running services

Compile the application using gradle:

```bash
./gradlew build
```

Then start all services (each in a different terminal):

```bash
java -jar gps/service/build/libs/tour-guide-gps.jar
java -jar rewards/service/build/libs/tour-guide-rewards.jar
java -jar users/service/build/libs/tour-guide-users.jar
```

### Testing

```bash
./gradlew test
```

### Updating documentation

```bash
./docs.sh generate
./docs.sh publish
```

## Deployment

Take a look at docker-compose.yml for an example of deployment. You can test it
by running:

```bash
docker-compose -p tourguide up --remove-orphans --build
```

![Deployment Overview](.readme/deployment.png?raw=true)

## Notes

This is a school project (for OpenClassrooms).

The goal is to resolve performance issues, split a monolithic application into
multiple services and deploy them with Docker.
