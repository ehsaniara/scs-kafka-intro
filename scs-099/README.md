# SCS-099

## Producer Consumer

A simple Example of an Event Driven Flow by the help of **SPRING CLOUD STREAM KAFKA**

##### properties

* java.version: `11`
* spring-cloud.version: `Hoxton.SR11` (To get Advantage of Binders `@Input`,`@Output`)
* spring-boot.version: `2.4.5`

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

## Run the Test2

```shell
java -Dspring.profiles.active=test2 -jar scs-099-0.0.1-SNAPSHOT.jar
```

## Run the Test3

```shell
java -Dspring.profiles.active=test3 -jar scs-099-0.0.1-SNAPSHOT.jar
```

## Run the Test4

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
