FROM eclipse-temurin:21-jre

ARG JAR_VERSION=0.1.0-SNAPSHOT

COPY ./target/sgr-intermediary-${JAR_VERSION}.jar sgr-intermediary.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/sgr-intermediary.jar"]
