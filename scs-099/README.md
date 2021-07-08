# SCS-099

## Producer Consumer

A simple example for an Event Driven Flow by the help of **SPRING CLOUD STREAM KAFKA**

![General Flow Diagram](material/kafka-events-intro-099-1.svg)

##### properties

* java.version: `11`
* spring-cloud.version: `Hoxton.SR11` (To get Advantage of Binders `@Input`,`@Output`)
* spring-boot.version: `2.4.5`

### Documentation
Please visit [Spring Cloud Stream Kafka (Part 1)](https://tanzu.vmware.com/developer/guides/event-streaming/spring-cloud-stream-kafka-p1/) for Project documentation



The Docker-compose file contains: single kafka and zookeeper. just simply run the following command

```shell
docker-compose up -d
```

_Note: docker-compose file is at the root of this project "/scs-kafka-intro"_

## Build the project

Run the First Test:

```shell
mvn clean package
```

## Run the Application

Then run the generated jar file in `target` folder, (so make sure you are in the same directory when you run the jar
file or give the full path)

```shell
java -jar scs-099-0.0.1-SNAPSHOT.jar
```

_the application starts to listen on port 8080. make sure that port is not occupied by any other app already, if is try
to pass the following parameter before `-jar` by adding `-Dserver.port=8081`_

Basically in this test, Producer adding 10 messages into kafka topic every 5 sec.

## Single Producer and Single Consumer

```shell
java -Dspring.profiles.active=test2 -jar scs-099-0.0.1-SNAPSHOT.jar
```

![General Flow Diagram](material/kafka-events-intro-099-2.svg)

## Single Producer and Single Consumer with 3 Thread

```shell
java -Dspring.profiles.active=test3 -jar scs-099-0.0.1-SNAPSHOT.jar
```

![General Flow Diagram](material/kafka-events-intro-099-4.svg)

## Single Producer and 3 Consumer App (3 separate JVM process)

Run the following codes in 3 different terminal

on Terminal-1: (this app has one Producer and one consumer)

```shell
java -Dspring.profiles.active=test2 -jar scs-099-0.0.1-SNAPSHOT.jar
```

on Terminal-2: (this app has only one consumer)

```shell
java -Dspring.profiles.active=test2 -Dserver.port=8081 -jar scs-099-0.0.1-SNAPSHOT.jar
```

on Terminal-3: (this app has only one consumer)

```shell
java -Dspring.profiles.active=test2 -Dserver.port=8082 -jar scs-099-0.0.1-SNAPSHOT.jar
```

![General Flow Diagram](material/kafka-events-intro-099-3.svg)


Please visit [Spring Cloud Stream Kafka (Part 1)](https://tanzu.vmware.com/developer/guides/event-streaming/spring-cloud-stream-kafka-p1/) for Project documentation
