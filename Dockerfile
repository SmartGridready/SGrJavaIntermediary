FROM eclipse-temurin:21-jre

COPY ./build/libs/sgr-intermediary.jar sgr-intermediary.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/sgr-intermediary.jar"]
