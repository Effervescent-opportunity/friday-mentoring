# Build stage
FROM gradle:8.2.0-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

# clean нет смысла выполнять, т.к. код а) скопирован б) в свежее окружение
# сборку удобнее выполнять по build без перечисления
# см. также
RUN gradle build --no-daemon

# Package stage
FROM bellsoft/liberica-openjdk-alpine:17.0.7
EXPOSE 8080

# 1. благодаря
# bootJar {
#  archiveFileName = 'application.jar'
#}
# в build.gradle - здесь можно получить предсказуемое имя fat jar и не копировать лишнее.
# По маске в предыдущей реализации копировались два артефакта:
# -- friday-mentoring-0.0.1-SNAPSHOT-plain.jar (что неверно);
# -- friday-mentoring-0.0.1-SNAPSHOT.jar (именно этот jar нужно было копировать).
COPY --from=build /home/gradle/src/build/libs/application.jar /usr/app/application.jar

# позволяет не запускать процесс от рута (см. также google "container escaping" о том, почему это важно)
USER 1000:1000
ENTRYPOINT ["java", "-jar", "/usr/app/application.jar"]
