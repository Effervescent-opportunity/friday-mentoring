
## Spring JPA

in Spring Boot 3 javax.persistence.Entity was renamed to jakarta.persistence.Entity. So with all annotations from javax

`spring.jpa.hibernate.ddl-auto=update` таблички будут созданы автоматически и будут меняться в соответствии с изменением модели. Для прода нужно `validation`
`spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true` his entry is put just to avoid a warning message in the logs when you start the spring-boot application. This bug is from hibernate which tries to retrieve some metadata from postgresql db and failed to find that and logs as a warning. It doesn't cause any issue though

## Postgres
### psql commands
`psql -d fmdb -U fmuser` connect to db with dbname fmdb, user fmuser

`\l` get list of databases

`\q` quit

### DB column types

`varchar(n)` column type - stores up to n characters (not bytes). If I have a string with length m<n, then будет храниться строка длины m
`char(n)` column type - stores n characters (not bytes). If I have a string with length m<n, then остальные символы будут заполнены пробелами. Is the slowest one
`text` column type - unlimited length

`json` column type - stores json as is, reparse on each execution
`jsonb` column type - stores decompiled binary format, slower to input, faster to process, supports indexing


## Swagger & OpenAPI
OpenAPI = Specification
Swagger = Tools for implementing specification

– For Spring Boot 3:
To use Swagger 3 in your Gradle project, you need to add the springdoc-openapi-starter-webmvc-ui dependency to your project’s build.gradle file:
implementation group: 'org.springdoc', name: 'springdoc-openapi-starter-webmvc-ui', version: '2.0.3'

there is also spring-restdocs

there is swagger-ui tool and openapi-generator tool and a lot of others link [OpenAPI Code generators](https://tools.openapis.org/categories/code-generators.html )
and springfox aaaaaaa
in this link https://devmark.ru/article/springdoc-rest-api it's said that springfox-swagger -> springdoc for new SpringBoot

i have this:
1. implementation 'io.springfox:springfox-boot-starter:3.0.0'
2. implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0'
3.

## Spring Boot test
`@SpringBootTest` loads all application context, all layers (web layer, service layer, and data access layer)
`@WebMvcTest` on the other hand, is used to test only the web layer of your application

https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#features.testing.spring-boot-applications.mocking-beans
CGLib proxies, such as those created for scoped beans, declare the proxied methods as final.
This stops Mockito from functioning correctly as it cannot mock or spy on final methods in its default configuration.
If you want to mock or spy on such a bean, configure Mockito to use its inline mock maker by adding org.mockito:mockito-inline
to your application’s test dependencies. This allows Mockito to mock and spy on final methods.

## Docker
### Docker commands

`docker -v` Docker version (I have Docker version 20.10.25, build 20.10.25-0ubuntu1~22.04.1)

`docker compose version` Docker compose version (I have Docker Compose version v2.20.2)

`docker system prune -a` - delete all images, containers and networks

`docker system prune -a --volumes` - delete all images, containers, networks and volumes

`docker volume prune -a` - delete all volumes

`docker ps` see all running containers

`docker ps -a` see all containers

`docker images -a` or `docker image ls` see all images

`docker volume ls` see all volumes

`docker volume inspect {volume name}` 

`docker logs -f {container-name}` see and follow container logs (if `-n 50` added then see only 50 lines)

`docker exec -i {container-id} bash` open bash for container

`docker rm -v {container-name}` remove container and it's anonymous volumes

`docker rmi {image id|image tag}` remove image

docker-compose

docker-compose logs kafka | grep -i started
docker logs -f <container-id>



docker port {container_name} - see ports from container

docker-compose up -d --no-deps --build <service_name>



docker compose build --no-cache mentoring-app
- `docker-compose build --no-cache mentoring-app` - recreate container

### Docker information

[DockerHub search](https://hub.docker.com/search?q=)

ports:
- HOST_PORT:CONTAINER_PORT
  [outer:inner]

networked service-to-service communication uses CONTAINER_PORT and service is accessible by HOST_PORT outside the swarm(?)
in Service image or build. If borh, then no guarantee thatpulled image is strictly the same with builded. Compose will try to build an image firstly

if you have container_name in service then you can't have two or more replicas of it. (you can't scale a service)
if you have your service A depends_on service B, then service B will be created before service A, and service A will be removed before B
And there is more options for this - you can set required false, restart policy and conditions when dependency is satisfying (https://docs.docker.com/compose/compose-file/05-services/#long-syntax-1)

### Docker storages

[Docker Storages documentation](https://docs.docker.com/storage/)

- __Volumes__ are stored in a part of the host filesystem which is managed by Docker (/var/lib/docker/volumes/ on Linux). Non-Docker processes should not modify this part of the filesystem. Volumes are the best way to persist data in Docker.
- __Bind mounts__ may be stored anywhere on the host system. They may even be important system files or directories. Non-Docker processes on the Docker host or a Docker container can modify them at any time.
- __tmpfs mounts__ are stored in the host system's memory only, and are never written to the host system's filesystem.

Вот такое значит, что volume db-data будет доступен в контейнере для serviceA по пути /etc/data
```
services:
  serviceA:
    volumes:
      - db-data:/etc/data
```

Есть еще имя, не понимаю пока, как связано с тем, что написано

```
volumes:
  db-data:
    name: "my-app-data"
```


## Security


------------------
you have to logout with cookie
I can logout with both GET and POST


PKCS12 because it's language-neutral and JKS is only for JAVA

why for anonymous user I get FORBIDDEN - 403
2023-07-27T19:17:10.659+03:00 DEBUG 5654 --- [    Test worker] w.c.HttpSessionSecurityContextRepository :
Retrieved SecurityContextImpl [Authentication=UsernamePasswordAuthenticationToken
[Principal=org.springframework.security.core.userdetails.User [Username=noRoot, Password=[PROTECTED], Enabled=true,
AccountNonExpired=true, credentialsNonExpired=true, AccountNonLocked=true, Granted Authorities=[ROLE_USER]],
Credentials=[PROTECTED], Authenticated=true, Details=null, Granted Authorities=[ROLE_USER]]]

2023-07-27T19:17:10.667+03:00 DEBUG 5654 --- [    Test worker] horizationManagerBeforeMethodInterceptor :
Failed to authorize ReflectiveMethodInvocation: public java.time.ZonedDateTime com.friday.mentoring.service.ClockService.getNowInUtc();
target is of class [com.friday.mentoring.service.ClockService] with authorization manager
org.springframework.security.config.annotation.method.configuration.DeferringObservationAuthorizationManager@67e11bda
and decision ExpressionAuthorizationDecision [granted=false, expressionAttribute=authentication.name == 'root' and hasRole('ADMIN')]

and for incorrect user I get NOT_FOUND - 404

2023-07-27T19:17:10.809+03:00 DEBUG 5654 --- [    Test worker] o.s.s.w.a.AnonymousAuthenticationFilter  :
Set SecurityContextHolder to anonymous SecurityContext
2023-07-27T19:17:10.810+03:00 DEBUG 5654 --- [    Test worker] horizationManagerBeforeMethodInterceptor :
Failed to authorize ReflectiveMethodInvocation: public java.time.ZonedDateTime com.friday.mentoring.service.ClockService.getNowInUtc();
target is of class [com.friday.mentoring.service.ClockService] with authorization manager
org.springframework.security.config.annotation.method.configuration.DeferringObservationAuthorizationManager@67e11bda
and decision ExpressionAuthorizationDecision [granted=false, expressionAttribute=authentication.name == 'root' and hasRole('ADMIN')]

Forbidden 403 access denied for incorrect user can be catched in
@ExceptionHandler(value = AccessDeniedException.class)
protected ProblemDetail handleAccessDenied(AccessDeniedException ex, WebRequest request) {
ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.ALREADY_REPORTED);
pd.setDetail(ex.getMessage());
pd.setType(URI.create(request.getContextPath()));
return pd;
}