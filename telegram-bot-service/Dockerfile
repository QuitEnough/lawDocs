FROM maven:3.8.8-eclipse-temurin-17 as build
WORKDIR /build
COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests -pl telegram-bot-service -am

FROM eclipse-temurin:17-jre
VOLUME /tmp
WORKDIR /app
COPY --from=build /build/telegram-bot-service/target/telegram-bot-service-0.1.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar"]