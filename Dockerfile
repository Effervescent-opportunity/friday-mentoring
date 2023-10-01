# Build stage
FROM gradle:8.3-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

# Package stage
FROM bellsoft/liberica-openjdk-alpine:17.0.8
EXPOSE 8443
COPY --from=build /home/gradle/src/build/libs/application.jar /usr/app/application.jar
# позволяет не запускать процесс от рута (см. также google "container escaping" о том, почему это важно)
USER 1000:1000
ENTRYPOINT ["java", "-jar", "/usr/app/application.jar"]

#todo не могу достучаться до приложения курлом, оно меня не видит
#*   Trying 127.0.0.1:8443...
 #* Connected to localhost (127.0.0.1) port 8443 (#0)
 #* ALPN, offering h2
 #* ALPN, offering http/1.1
 #* TLSv1.0 (OUT), TLS header, Certificate Status (22):
 #* TLSv1.3 (OUT), TLS handshake, Client hello (1):
 #* TLSv1.0 (OUT), TLS header, Unknown (21):
 #* TLSv1.3 (OUT), TLS alert, decode error (562):
 #* error:0A000126:SSL routines::unexpected eof while reading
 #* Closing connection 0
 #curl: (35) error:0A000126:SSL routines::unexpected eof while reading
 # и в логах приложения пусто