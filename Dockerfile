# Build stage
FROM gradle:8.2.1-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

# Package stage
FROM bellsoft/liberica-openjdk-alpine:17.0.8
EXPOSE 8080
COPY --from=build /home/gradle/src/build/libs/application.jar /usr/app/application.jar
# позволяет не запускать процесс от рута (см. также google "container escaping" о том, почему это важно)
USER 1000:1000
ENTRYPOINT ["java", "-jar", "/usr/app/application.jar"]
