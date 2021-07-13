FROM maven:3.6.3-jdk-11-slim AS maven_builder
COPY pom.xml /application/
COPY src /application/src/
WORKDIR /application/
RUN mvn clean package -e

# extract spring boot the layers
FROM openjdk11:jre-slim as jar_builder
WORKDIR /application
COPY --from=maven_builder /application/target/*.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

# copy the extracted layers
FROM openjdk11:jre-slim
WORKDIR /application
COPY --from=jar_builder application/dependencies/ ./
COPY --from=jar_builder application/spring-boot-loader/ ./
COPY --from=jar_builder application/snapshot-dependencies/ ./
COPY --from=jar_builder application/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
