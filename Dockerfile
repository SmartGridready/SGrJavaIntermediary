FROM eclipse-temurin:17-jdk
ARG JAR_FILE
COPY ${JAR_FILE} sgr-intermediary.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/sgr-intermediary.jar"]
