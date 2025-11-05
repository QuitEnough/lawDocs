FROM maven:3.8.8-eclipse-temurin-17 as build
WORKDIR /build
COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests -pl file-storage-service -am

FROM eclipse-temurin:17-jre
VOLUME /tmp
WORKDIR /app
COPY --from=build /build/file-storage-service/target/file-storage-service-0.1.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]