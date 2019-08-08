# Zipkin demo

## Introduction

When working within a distributed system, or simply into a system calling other component than itself (database, queue, rest endpoint, ...), it is difficult to understand what happen wihtout looking deeply in the code.
For example, if a call takes a lot of time, we need metrics to understand where and how much time the process is lagging.
Zipkin is an answer to this problem.
Zipkin allows tracing calls into a distributed system.

## ROADMAP

- HTTP call

|        | direct call | nested call |
| ------ | :---------: | :---------: |
| Java   |      x      |      x      |
| NodeJS |             |             |
| PHP    |             |             |

- Message queue

|        | ActiveMQ | Kafka |
| ------ | :------: | :---: |
| Java   |          |       |
| NodeJS |          |       |
| PHP    |          |       |

- Database

|        | MySQL |
| ------ | :---: |
| Java   |       |
| NodeJS |       |
| PHP    |       |

## Demo

### Java-basic

This demo illustrates the simpliest use case : a java application acting as a web server.
All call to the endpoint are traced into zipking

```shell
docker-compose -f _docker-compose/java-basic.yml up
```

Services availables :

| Service name        | URL                       |
| ------------------- | ------------------------- |
| zipkin              | http://p-nan-roseau:9411  |
| java-basic-frontend | http://p-nan-roseau:8080/ |

Calling _java-basic-frontend_ gets you a serialized date.
This simple HTTP call is traced inside Zipkin.

### Java-resttemplate
