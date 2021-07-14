#Jay Ehsaniara, Dockerfile has to be in project root where pom.xml located
FROM maven:3.6.3-jdk-11-slim AS maven_builder
ARG GITHUB_TOKEN
COPY /pom.xml /application/
COPY /src /application/src/
WORKDIR /application/
RUN \
    if [ "x$GITHUB_TOKEN" = "x" ] ; then \
    echo "Argument not provided" ; \
    else\
    echo "<settings><servers><server><id>github</id><username>OWNER</username><password>${GITHUB_TOKEN}</password></server></servers></settings>" > settings.xml ; \
    sed -i 's|</project>|<repositories><repository><id>github</id><name>GitHub</name><url>https://maven.pkg.github.com/ehsaniara/scs-kafka-intro</url></repository></repositories></project>|g' pom.xml ; \
    fi
RUN if [ "x$GITHUB_TOKEN" = "x" ] ; then mvn clean package ; else  mvn clean package -s settings.xml ; fi
RUN if [ "x$GITHUB_TOKEN" = "x" ] ; then echo "Argument not provided" ; else  rm settings.xml ; fi

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
