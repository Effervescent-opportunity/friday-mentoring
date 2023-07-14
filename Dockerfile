# Build stage
FROM gradle:8.2.0-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle clean test bootJar --no-daemon

# Package stage
FROM bellsoft/liberica-openjdk-alpine:17.0.7
EXPOSE 8080
COPY --from=build /home/gradle/src/build/libs/*.jar /usr/app/spring-boot-application.jar
ENTRYPOINT ["java", "-jar", "/usr/app/spring-boot-application.jar"]
