# Build stage
FROM gradle:8.2.0-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle test bootJar --no-daemon

# Package stage
FROM bellsoft/liberica-openjdk-alpine:17.0.7
EXPOSE 8080
#RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /usr/app/spring-boot-application.jar
ENTRYPOINT ["java", "-jar", "/usr/app/spring-boot-application.jar"]

#ENV JAR_NAME=app.jar
 #ENV APP_HOME=/usr/app/
 #WORKDIR $APP_HOME
 #COPY --from=BUILD $APP_HOME .
 #EXPOSE 8080
 #ENTRYPOINT exec java -jar $APP_HOME/build/libs/$JAR_NAME
#WTF CONTAINER ID   IMAGE          COMMAND                  CREATED         STATUS         PORTS      NAMES
     #1aa7d2d28268   21b73d488789   "java -jar /usr/app/…"   3 minutes ago   Up 3 minutes   8080/tcp   heuristic_khayyam
 #todo make normal name & what about no-daemon & try to run from terinal 
 #docker ps - see running containers
 #docker stop balblabla - stop container




