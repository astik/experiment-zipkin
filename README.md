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
| NodeJS | with Express    | with Axios        |
| PHP    |                 |                   |

- Message queue producer

|        | ActiveMQ                   | RabbitMQ | Kafka                      |
| ------ | -------------------------- | -------- | -------------------------- |
| Java   | with Spring Sleuth and JMS |          | with Spring Sleuth and JMS |
| NodeJS |                            |          | with KafkaJS               |
| PHP    |                            |          |                            |

- Message queue consumer

|        | ActiveMQ                   | RabbitMQ | Kafka                      |
| ------ | -------------------------- | -------- | -------------------------- |
| Java   | with Spring Sleuth and JMS |          | with Spring Sleuth and JMS |
| NodeJS |                            |          | with KafkaJS               |
| PHP    |                            |          |                            |

- Database

|        | MySQL      |
| ------ | ---------- |
| Java   | with P6Spy |
| NodeJS |            |
| PHP    |            |

## Demo

### Java-basic

This demo illustrates the simpliest use case : a Java application acting as a web server.
All calls to the endpoint are traced into Zipkin.

```shell
docker-compose -f _docker-compose/java-basic.yml up
```

Services availables :

| Service name        | URL                   |
| ------------------- | --------------------- |
| zipkin              | http://[MY_HOST]:9411 |
| java-basic-frontend | http://[MY_HOST]:8080 |

Calling _java-basic-frontend_ gets you a serialized date.
This simple HTTP call is traced inside Zipkin.

### Java-resttemplate

This demo illustrates nested HTTP calls in Java using RestTemplate.
Once the main endpoint is called, it will call another service.
All calls to any endpoint are traced into Zipkin.

```shell
docker-compose -f _docker-compose/java-resttemplate.yml up
```

Services availables :

| Service name               | URL                   |
| -------------------------- | --------------------- |
| zipkin                     | http://[MY_HOST]:9411 |
| java-basic-frontend        | http://[MY_HOST]:8081 |
| java-resttemplate-frontend | http://[MY_HOST]:8080 |

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

| Service name             | URL                   |
| ------------------------ | --------------------- |
| zipkin                   | http://[MY_HOST]:9411 |
| java-basic-frontend      | http://[MY_HOST]:8081 |
| java-httpclient-frontend | http://[MY_HOST]:8080 |

Only the implementation for the nested call is modified.
Instead of using Spring's RestTemplate, Apache's HttpClient is used.

### Java-activemq

This demo illustrates how Spring sleuth decorates _JmsTemplate_ and _JmsListener_.

```shell
docker-compose -f _docker-compose/java-activemq.yml up
```

Services availables :

| Service name           | URL                   |
| ---------------------- | --------------------- |
| zipkin                 | http://[MY_HOST]:9411 |
| activemq               | http://[MY_HOST]:8161 |
| java-activemq-frontend | http://[MY_HOST]:8080 |
| java-activemq-consumer | not reachable         |

Calling java-activemq-frontend will send a message onto the message queue.
You can check that the message is correctly sent through ActiveMQ UI (default credentials : admin/admin).
A Java application is defined to consume message from the queue.
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

| Service name             | URL                   |
| ------------------------ | --------------------- |
| zipkin                   | http://[MY_HOST]:9411 |
| activemq                 | http://[MY_HOST]:8161 |
| java-activemq-frontend   | http://[MY_HOST]:8080 |
| java-activemq-consumer-1 | not reachable         |
| java-activemq-consumer-2 | not reachable         |

It would have been great to be able use _docker-compose scale_ to scale up (or down) _java-activemq-consumer_.
But it does not seem to have a simple way to have container ID as environment variable (in order to change application name for demo's purpose).

### Java-kafka

This demo illustrates how Spring sleuth decorates _KafkaTemplate_ and _KafkaListener_.

```shell
docker-compose -f _docker-compose/java-kafka.yml up
```

Services availables :

| Service name                 | URL                   |
| ---------------------------- | --------------------- |
| zipkin                       | http://[MY_HOST]:9411 |
| kafka                        | not reachable         |
| zookeeper (needed for kafka) | not reachable         |
| java-activemq-frontend       | http://[MY_HOST]:8080 |
| java-activemq-consumer       | not reachable         |

Calling _java-kafka-frontend_ will send a message onto the message queue (topic is _topicBackend_).
Frontend service returns the raw Kafka result from the sending.
A Java application is defined to consume message from the queue (topic is _topicBackend_ and groupId is _my-java-consumer-group_).
Consumption is very simple, it will dump message on the standard output.
Sending and consuming message are traced through Zipkin.
For each call on _java-kafka-frontend_, you should observe 4 spans : two for _java-kafka-frontend_ endpoint and its sending to the queue and two for _java-kafka-consumer_ message consumption.

### Java-mysql

This demo illustrates how P6Spy is used through brave instrumentation to decorate JDBC datasource.

```shell
docker-compose -f _docker-compose/java-mysql.yml up
```

Services availables :

| Service name        | URL                                               |
| ------------------- | ------------------------------------------------- |
| zipkin              | http://[MY_HOST]:9411                             |
| mysql               | port 3306 is accessible                           |
| adminer             | http://[MY_HOST]:8081 (for demo only, not needed) |
| java-mysql-frontend | http://[MY_HOST]:8080                             |

Application _java-mysql-frontend_ offers 2 endpoint :

- GET / : will retrieve customers from database
- POST / : will create customers into database

Calling _java-mysql-frontend_ with a POST will trigger mulitple JDBC call in order to insert 5 new customers.
In Zipkin UI, calls to database are traced and you can witness how many database calls are made (especially those for the hibernate sequence).

Calling _java-mysql-frontend_ with a GET will trigger only one JDBC call in order to fetch customers.

For each traced database call, we can check which DB query is made wit its parameter.

Interesting thing to notice is how little you have to project to make it work with P6Spy, no need to change code:

- change JDBC driver
- change JDBC URL
- add a property file for P6Spy
- add P6Spy Maven dependency

### NodeJS-basic

This demo illustrates the simpliest use case : a NodeJS application acting as a web server (_express_).
All calls to the endpoint are traced into Zipkin.

```shell
docker-compose -f _docker-compose/nodejs-basic.yml up
```

Services availables :

| Service name          | URL                   |
| --------------------- | --------------------- |
| zipkin                | http://[MY_HOST]:9411 |
| nodejs-basic-frontend | http://[MY_HOST]:9000 |

Calling _nodejs-basic-frontend_ gets you a serialized date.
This simple HTTP call is traced inside Zipkin.
As debug is enabled for the demo, you can see details for the HTTP call to Zipkin.

### NodeJS-axios

This demo illustrates nested HTTP calls in NodeJS using Axios.
Once the main endpoint is called, it will call another service.
All calls to any endpoint are traced into Zipkin.

```shell
docker-compose -f _docker-compose/nodejs-axios.yml up
```

Services availables :

| Service name          | URL                   |
| --------------------- | --------------------- |
| zipkin                | http://[MY_HOST]:9411 |
| nodejs-basic-frontend | http://[MY_HOST]:9001 |
| nodejs-axios-frontend | http://[MY_HOST]:9000 |

Calling _nodejs-axios-frontend_ will call _nodejs-basic-frontend_.
Then a serialized date is brought back from _nodejs-basic-frontend_ to _nodejs-axios-frontend_ and then to user.
All HTTP calls are traced inside Zipkin.
The server acting as backend for the nested HTTP call is the one used in the _nodejs-basic_ demo.

Bonus, you can try the fibonacci endpoint to trigger latency :

- http://[MY_HOST]:9000/fibonacci?count=40

### NodeJS-kafka

This demo illustrates how ZipkinJS can be used to decorated a KafkaJS client.

```shell
docker-compose -f _docker-compose/nodejs-kafkajs.yml up
```

Services availables :

| Service name                 | URL                   |
| ---------------------------- | --------------------- |
| zipkin                       | http://[MY_HOST]:9411 |
| kafka                        | not reachable         |
| zookeeper (needed for kafka) | not reachable         |
| nodejs-kafkajs-frontend      | http://[MY_HOST]:9000 |
| nodejs-kafkajs-consumer      | not reachable         |

Calling _nodejs-kafkajs-frontend_ will send a message onto the message queue (topic is _topicBackend_).
Frontend service returns _OK_.
A NodeJS application is defined to consume message from the queue (topic is _topicBackend_ and groupId is _my-nodejs-consumer-group_).
Consumption is very simple, it will dump message on the standard output.
Sending and consuming message are traced through Zipkin.
For each call on _nodejs-kafkajs-frontend_, you should observe 4 spans : two for _nodejs-kafkajs-frontend_ endpoint and its sending to the queue and two for _nodejs-kafkajs-consumer_ message consumption.

## Documentation

- introdution to distributed tracing : https://speakerdeck.com/adriancole/introduction-to-distributed-tracing-and-zipkin-at-devopsdays-singapore
- example for Java Spring : https://github.com/openzipkin/sleuth-webmvc-example
- tools and example for NodeJS (it contains multiple instrumentations) : https://github.com/openzipkin/zipkin-js-example
- tools and example for PHP : https://github.com/openzipkin/zipkin-php

## TODO

- plug nginx with zipkin : https://medium.com/opentracing/how-to-enable-nginx-for-distributed-tracing-9479df18b22c
