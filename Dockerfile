
FROM maven:3 as build
WORKDIR /build
COPY src src
COPY pom.xml pom.xml
COPY checkstyle.xml checkstyle.xml
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests

FROM openjdk:17
VOLUME /tmp
ARG JAR_FILE=cloud-0.1.jar
WORKDIR /app
COPY --from=build /build/target/$JAR_FILE app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
