FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /build
COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests -pl api -am

FROM eclipse-temurin:17-jre
VOLUME /tmp
WORKDIR /app
COPY --from=build /build/api/target/api-0.1.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]