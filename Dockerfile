FROM eclipse-temurin:17-jre
ARG JAR_FILE
COPY ./rest/target/rest-0.0.1-SNAPSHOT.jar sgr-intermediary.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/sgr-intermediary.jar"]
