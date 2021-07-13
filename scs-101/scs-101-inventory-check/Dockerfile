FROM maven:3.6.3-jdk-11-slim AS maven_builder
ARG GITHUB_TOKEN
COPY pom.xml /application/
COPY src /application/src/
WORKDIR /application/
RUN echo "<settings><servers><server><id>github</id><username>OWNER</username><password>${GITHUB_TOKEN}</password></server></servers></settings>" > settings.xml
RUN mvn clean package -s settings.xml -e

# extract spring boot the layers
FROM openjdk:11-jdk-slim as jar_builder
WORKDIR /application
COPY --from=maven_builder /application/target/*.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

# copy the extracted layers
FROM openjdk:11-jdk-slim
WORKDIR /application
COPY --from=jar_builder application/dependencies/ ./
COPY --from=jar_builder application/spring-boot-loader/ ./
COPY --from=jar_builder application/snapshot-dependencies/ ./
COPY --from=jar_builder application/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
