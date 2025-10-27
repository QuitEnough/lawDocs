FROM maven:3.9.9-eclipse-temurin-17 as build
WORKDIR /build
COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn clean install -DskipTests -N
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests -pl db-service -am

FROM openjdk:17
VOLUME /tmp
ARG JAR_FILE=db-service-0.1.jar
WORKDIR /app
COPY --from=build /build/db-service/target/$JAR_FILE app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]