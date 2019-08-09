# Zipkin demo

## Introduction

When working within a distributed system, or simply into a system calling other component than itself (database, queue, rest endpoint, ...), it is difficult to understand what happen wihtout looking deeply in the code.
For example, if a call takes a lot of time, we need metrics to understand where and how much time the process is lagging.
Zipkin is an answer to this problem.
Zipkin allows tracing calls into a distributed system.

## ROADMAP

- HTTP call

|        | direct call     | nested call       |
| ------ | --------------- | ----------------- |
| Java   | with Spring MVC | with RestTemplate |
|        |                 | with HttpClient   |
| NodeJS |                 |                   |
| PHP    |                 |                   |

- Message queue

|        | ActiveMQ | Kafka |
| ------ | -------- | ----- |
| Java   |          |       |
| NodeJS |          |       |
| PHP    |          |       |

- Database

|        | MySQL |
| ------ | ----- |
| Java   |       |
| NodeJS |       |
| PHP    |       |

## Demo

### Java-basic

This demo illustrates the simpliest use case : a java application acting as a web server.
All call to the endpoint are traced into zipking.

```shell
docker-compose -f _docker-compose/java-basic.yml up
```

Services availables :

| Service name        | URL                      |
| ------------------- | ------------------------ |
| zipkin              | http://p-nan-roseau:9411 |
| java-basic-frontend | http://p-nan-roseau:8080 |

Calling _java-basic-frontend_ gets you a serialized date.
This simple HTTP call is traced inside Zipkin.

### Java-resttemplate

This demo illustrates nested HTTP calls in java using RestTemplate.
Once the main endpoint is called, it will call another service.
All call to any endpoint are traced into zipking.

```shell
docker-compose -f _docker-compose/java-resttemplate.yml up
```

Services availables :

| Service name               | URL                      |
| -------------------------- | ------------------------ |
| zipkin                     | http://p-nan-roseau:9411 |
| java-basic-frontend        | http://p-nan-roseau:8081 |
| java-resttemplate-frontend | http://p-nan-roseau:8080 |

Calling _java-resttemplate-frontend_ will call _java-basic-frontend_.
Then a serialized date is brought back from _java-basic-frontend_ to _java-resttemplate-frontend_ and then to user.
All HTTP calls are traced inside Zipkin.
The server acting as backend for the nested HTTP call is the one used in the _java-basic_ demo.

### Java-httpclient

This demo is the same as the _java-resttemplate_ one.

Services availables :

| Service name             | URL                      |
| ------------------------ | ------------------------ |
| zipkin                   | http://p-nan-roseau:9411 |
| java-basic-frontend      | http://p-nan-roseau:8081 |
| java-httpclient-frontend | http://p-nan-roseau:8080 |

Only the implementation for the nested call is modified.
Instead of using Spring's RestTemplate, Apache's HttpClient is used.
