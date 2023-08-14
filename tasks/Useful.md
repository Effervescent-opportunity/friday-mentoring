
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

## Docker
### Docker commands

`docker -v` Docker version (I have Docker version 20.10.25, build 20.10.25-0ubuntu1~22.04.1)

`docker compose version` Docker compose version (I have Docker Compose version v2.20.2)

`docker system prune -a` - delete all images, containers, volumes and networks

`docker ps` see all running containers

docker-compose

docker-compose logs kafka | grep -i started
docker logs -f <container-id>



docker port {container_name} - see ports from container

docker-compose up -d --no-deps --build <service_name>

docker logs -f {container-name} -

docker compose build --no-cache mentoring-app
- `docker-compose build --no-cache mentoring-app` - recreate container

### Docker information

[DockerHub search](https://hub.docker.com/search?q=)

ports: 
- HOST_PORT:CONTAINER_PORT

networked service-to-service communication uses CONTAINER_PORT and service is accessible by HOST_PORT outside the swarm(?)
in Service image or build. If borh, then no guarantee thatpulled image is strictly the same with builded. Compose will try to build an image firstly

if you have container_name in service then you can't have two or more replicas of it. (you can't scale a service)
if you have your service A depends_on service B, then service B will be created before service A, and service A will be removed before B
And there is more options for this - you can set required false, restart policy and conditions when dependency is satisfying (https://docs.docker.com/compose/compose-file/05-services/#long-syntax-1)

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