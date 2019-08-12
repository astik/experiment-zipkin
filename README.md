# Zipkin demo

## Introduction

When working within a distributed system, or simply into a system calling other component than itself (database, queue, rest endpoint, ...), it is difficult to understand what happen wihtout looking deeply in the code.
For example, if a call takes a lot of time, we need metrics to understand where and how much time the process is lagging.
Zipkin is an answer to this problem.
Zipkin allows tracing calls into a distributed system.

This project is not at all about ho to manage system element, it is only about connectivity between those components.

## ROADMAP

- HTTP call

|        | direct call     | nested call       |
| ------ | --------------- | ----------------- |
| Java   | with Spring MVC | with RestTemplate |
|        |                 | with HttpClient   |
| NodeJS |                 |                   |
| PHP    |                 |                   |

- Message queue producer

|        | ActiveMQ                   | RabbitMQ | Kafka                      |
| ------ | -------------------------- | -------- | -------------------------- |
| Java   | with Spring Sleuth and JMS |          | with Spring Sleuth and JMS |
| NodeJS |                            |          |
| PHP    |                            |          |                            |

- Message queue consumer

|        | ActiveMQ                   | RabbitMQ | Kafka                      |
| ------ | -------------------------- | -------- | -------------------------- |
| Java   | with Spring Sleuth and JMS |          | with Spring Sleuth and JMS |
| NodeJS |                            |          |                            |
| PHP    |                            |          |                            |

- Database

|        | MySQL |
| ------ | ----- |
| Java   |       |
| NodeJS |       |
| PHP    |       |

## Demo

### Java-basic

This demo illustrates the simpliest use case : a java application acting as a web server.
All calls to the endpoint are traced into zipking.

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
All calls to any endpoint are traced into zipking.

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

```shell
docker-compose -f _docker-compose/java-httpclient.yml up
```

Services availables :

| Service name             | URL                      |
| ------------------------ | ------------------------ |
| zipkin                   | http://p-nan-roseau:9411 |
| java-basic-frontend      | http://p-nan-roseau:8081 |
| java-httpclient-frontend | http://p-nan-roseau:8080 |

Only the implementation for the nested call is modified.
Instead of using Spring's RestTemplate, Apache's HttpClient is used.

### Java-activemq

This demo illustrates how Spring sleuth decorates _JmsTemplate_ and _JmsListener_.

```shell
docker-compose -f _docker-compose/java-activemq.yml up
```

Services availables :

| Service name           | URL                      |
| ---------------------- | ------------------------ |
| zipkin                 | http://p-nan-roseau:9411 |
| activemq               | http://p-nan-roseau:8161 |
| java-activemq-frontend | http://p-nan-roseau:8080 |
| java-activemq-consumer | not reachable            |

Calling java-activemq-frontend will send a message onto the message queue.
You can check that the message is correctly sent through ActiveMQ UI (default credentials : admin/admin).
A java application is defined to consume message from the queue.
Consumption is very simple, it will dump message on the standard output.
Sending and consuming message are traced through Zipkin.
For each call on _java-activemq-frontend_, you should observe 3 spans : two for _java-activemq-frontend_ endpoint and its sending to the queue and one for _java-activemq-consumer_ message consumption.

### Java-activemq-multiple-consumers

This demo is the same as the _java-activemq_ one.
This time, 2 consumers are started.

```shell
docker-compose -f _docker-compose/java-activemq-multiple-consumers.yml up
```

Services availables :

| Service name             | URL                      |
| ------------------------ | ------------------------ |
| zipkin                   | http://p-nan-roseau:9411 |
| activemq                 | http://p-nan-roseau:8161 |
| java-activemq-frontend   | http://p-nan-roseau:8080 |
| java-activemq-consumer-1 | not reachable            |
| java-activemq-consumer-2 | not reachable            |

It would have been great to be able use _docker-compose scale_ to scale up (or down) _java-activemq-consumer_.
But it does not seem to have a simple way to have container ID as environment variable (in order to change application name for demo's purpose).

### Java-kafka

This demo illustrates how Spring sleuth decorates _KafkaTemplate_ and _KafkaListener_.

```shell
docker-compose -f _docker-compose/java-kafka.yml up
```

Services availables :

| Service name                 | URL                      |
| ---------------------------- | ------------------------ |
| zipkin                       | http://p-nan-roseau:9411 |
| kafka                        | not reachable            |
| zookeeper (needed for kafka) | not reachable            |
| java-activemq-frontend       | http://p-nan-roseau:8080 |
| java-activemq-consumer       | not reachable            |

Calling java-kafka-frontend will send a message onto the message queue (topic is _topicBackend_).
Frontend service returns the raw Kafka result from the sending.
A java application is defined to consume message from the queue (topic is _topicBackend_ and groupId is _backend_).
Consumption is very simple, it will dump message on the standard output.
Sending and consuming message are traced through Zipkin.
For each call on _java-kafka-frontend_, you should observe 4 spans : two for _java-kafka-frontend_ endpoint and its sending to the queue and two for _java-kafka-consumer_ message consumption.

### NodeJS-basic

This demo illustrates the simpliest use case : a NodeJS application acting as a web server (_express_).
All calls to the endpoint are traced into zipking.

```shell
docker-compose -f _docker-compose/nodejs-basic.yml up
```

Services availables :

| Service name          | URL                      |
| --------------------- | ------------------------ |
| zipkin                | http://p-nan-roseau:9411 |
| nodejs-basic-frontend | http://p-nan-roseau:9000 |

Calling _nodejs-basic-frontend_ gets you a serialized date.
This simple HTTP call is traced inside Zipkin.
As debug is enabled for the demo, you can see details for the HTTP call to zipkin.

## Documentation

- example for Java Spring : https://github.com/openzipkin/sleuth-webmvc-example
- tools and example for NodeJS : https://github.com/openzipkin/zipkin-js-example
- tools and example for PHP : https://github.com/openzipkin/zipkin-php
