FROM amazoncorretto:11
WORKDIR /app
COPY target/*.jar shareit-gateway.jar
ENTRYPOINT ["java","-jar","shareit-gateway.jar"]
