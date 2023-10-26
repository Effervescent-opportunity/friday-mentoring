-- 2023-10-26
https://localhost:8443/auth/events?ip=127.0.0.1&size=40&sort=user,desc

https://localhost:8443/auth/events1?ip=127.0.0.1&size=40&sort=userName,desc

-- 2023-10-25
Swagger works at https://localhost:8443/swagger-ui/index.html#/auth-event-controller/aaaa и даты там можно с плюсиком вводить
(и их в принципе можно без миллисекунд вводить)
curl -k -v -X POST https://localhost:8443/auth/login -H "Content-Type: application/json" -d '{"user": "root", "password": "password"}'

7BE9537CFB7B015F46EA2F0C8AA521C3

curl -k -v https://localhost:8444/actuator/auditevents ??? because of management.server.port = 8444
curl -k -v https://localhost:8443/actuator/auditevents after deletion


curl -k -v https://localhost:8443/time/current/utc 
curl -k -v https://localhost:8443/time/current/utc -H "Cookie: JSESSIONID=7BE9537CFB7B015F46EA2F0C8AA521C3" - works

Enormous page:
```json
{
  "content": [
    {
      "id": "fcf50e3e-4f6e-42a7-aaac-0ccc7d9c1cc2",
      "ipAddress": "127.0.0.1",
      "eventTime": "2023-09-28T19:00:16.995115Z",
      "userName": "root",
      "eventType": "0"
    }
  ],
  "pageable": {
    "sort": {
      "empty": true,
      "sorted": false,
      "unsorted": true
    },
    "offset": 0,
    "pageNumber": 0,
    "pageSize": 10,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 1,
  "totalElements": 1,
  "last": true,
  "size": 10,
  "number": 0,
  "sort": {
    "empty": true,
    "sorted": false,
    "unsorted": true
  },
  "numberOfElements": 1,
  "first": true,
  "empty": false
}
```

curl -k -v https://localhost:8443/auth/events - works

curl -k -v https://localhost:8443/auth/events?user=root
curl -k -v https://localhost:8443/auth/events?user=anonymousUser
https://localhost:8443/auth/events?user=wtfUser - empty + 200
https://localhost:8443/auth/events?user=root1&ip=127.0.0.1 - empty
https://localhost:8443/auth/events?user=root&ip=127.0.0.1 - has things
https://localhost:8443/auth/events?user=root&ip=127.0.0.1&type=AUTHENTICATION_SUCCESS - has things
https://localhost:8443/auth/events?user=root&page=0&size=2 - works
curl -k -v https://localhost:8443/auth/events?user=root&page=3&size=0 - wtf
https://localhost:8443/auth/events?ip=127.0.0.1&size=40&sort=user,desc

https://localhost:8443/auth/events?ip=127.0.0.1&size=40&sort=user,desc&sort=type,asc&sort=ab,abs

https://localhost:8443/auth/events?ip=127.0.0.1&size=40&dateFrom=2015-01-01T16:33:23&dateTo=2016-03-07T14:55:32 - 400 Failed to convert 'dateFrom' with value: '2015-01-01T16:33:23'"

https://localhost:8443/auth/events?ip=127.0.0.1&size=40&dateFrom=2015-01-01T16:33:23+02:00&dateTo=2016-03-07T14:55:32

https://localhost:8443/auth/events?ip=127.0.0.1&size=40&dateFrom=2000-10-31T01:30:00.000-05:00 - this works
https://localhost:8443/auth/events?ip=127.0.0.1&size=40&dateFrom=2023-10-24T01:30:00.000-05:00 - this works
https://localhost:8443/auth/events?ip=127.0.0.1&size=40&dateFrom=2023-10-24T01:30:00.000Z - this works

curl -k -v 'https://localhost:8443/auth/events?ip=127.0.0.1&size=40&dateFrom=2023-10-24T01:30:00.000%2b05:00'
https://localhost:8443/auth/events?ip=127.0.0.1&size=40&dateFrom=2023-10-24T01:30:00.000%2b05:00 - works

https://localhost:8443/auth/events?ip=127.0.0.1&size=40&dateTo=2023-10-24T01:30:00.000%2b05:00&dateFrom=2023-10-01T01:30:00.000%2b05:00

--2023-10-09
With running with dependency: https://localhost:8443/v3/api-docs
{"openapi":"3.0.1","info":{"title":"OpenAPI definition","version":"v0"},"servers":[{"url":"https://localhost:8443",
"description":"Generated server url"}],"paths":{"/auth/login":{"post":{"tags":["auth-controller"],"operationId":"login",
"requestBody":{"content":{"application/json":{"schema":{"$ref":"#/components/schemas/Credentials"}}},"required":true},
"responses":{"200":{"description":"OK"}}}},"/time/current":{"get":{"tags":["clock-controller"],"operationId":
"getCurrentDateTimeInTimezone","parameters":[{"name":"timezone","in":"query","required":true,"schema":{"type":"string"}}],
"responses":{"200":{"description":"OK","content":{"*/*":{"schema":{"$ref":"#/components/schemas/CurrentTimeResponse"}}}}}}},
"/time/current/utc":{"get":{"tags":["clock-controller"],"operationId":"getCurrentDateTimeInUtc",
"responses":{"200":{"description":"OK","content":{"*/*":{"schema":{"$ref":"#/components/schemas/CurrentTimeResponse"}}}}}}}},
"components":{"schemas":{"Credentials":{"type":"object","properties":{"user":{"type":"string"},"password":{"type":"string"}}},
"CurrentTimeResponse":{"type":"object","properties":{"timestamp":{"type":"string","format":"date-time"}}}}}}

On https://localhost:8443/swagger-ui/index.html#/ I have real swagger ui
And if I login with correct credentials, browser & Swagger remember jsessionid and I can get time! (and I can't see jsessionid in
swagger ui, only in development tool)

Also I have https://localhost:8443/actuator and https://localhost:8443/actuator/auditevents with interesting things:
{"timestamp":"2023-10-09T17:02:43.298786428Z","principal":"string","type":"AUTHENTICATION_FAILURE",
"data":{"type":"org.springframework.security.authentication.BadCredentialsException","message":"Bad credentials",
"details":{"remoteAddress":"127.0.0.1","sessionId":"F8ED791A87304D9B128D96AA15851450"}}}

management.endpoints.web.exposure.include=auditevents, openapi, swagger-ui
springdoc.show-actuator=true
springdoc.use-management-port=true
#only above 2 -> fails Parameter 3 of method indexPageTransformer in org.springdoc.webmvc.ui.SwaggerConfig required a bean
# of type 'org.springdoc.webmvc.ui.SwaggerWelcomeCommon' that could not be found.
management.server.port=8444
Так у меня 404 на https://localhost:8444/actuator/openapi
и сваггер не работает, и тут 404 https://localhost:8444/v3/api-docs
да ну его нафиг, cors включать еще в java конфигах где-то. можно погуглить конечно, но зачем делать больше, чем надо, тем более неинтересного
--

REPOSITORY                  TAG                  IMAGE ID       CREATED        SIZE
<none>                      <none>               f87b4ba628ef   3 days ago     542MB
<none>                      <none>               7a0673f0c0af   3 days ago     542MB
<none>                      <none>               eccb0f0a9746   3 days ago     561MB
<none>                      <none>               472650b4381c   3 days ago     561MB
<none>                      <none>               cd8020ac9ae0   3 days ago     542MB
<none>                      <none>               84d26bcfc8b8   3 days ago     542MB
<none>                      <none>               1eb92af1329b   3 days ago     540MB
<none>                      <none>               752fcb3f853a   3 days ago     540MB
<none>                      <none>               3c357f84e0bf   3 days ago     540MB
<none>                      <none>               2d086bdfdb03   3 days ago     540MB
<none>                      <none>               152969bf031e   3 days ago     536MB
<none>                      <none>               3211da717bcd   3 days ago     536MB

CONTAINER ID   IMAGE                             COMMAND                  CREATED       STATUS                     PORTS     NAMES
0faaa2cc06de   postgres:15.4-alpine              "docker-entrypoint.s…"   2 weeks ago   Exited (0) 2 hours ago               fm-postgres
55b5b6ee79e4   provectuslabs/kafka-ui:v0.7.1     "/bin/sh -c 'java --…"   5 weeks ago   Exited (143) 2 hours ago             fm-kafka-ui
e1124431cf4b   confluentinc/cp-kafka:7.4.1       "/etc/confluent/dock…"   5 weeks ago   Exited (143) 2 hours ago             fm-kafka
6bfb3037f7da   confluentinc/cp-zookeeper:7.4.1   "/etc/confluent/dock…"   5 weeks ago   Exited (143) 2 hours ago             fm-zookeeper


docker inspect -f '{{ .Mounts }}' fm-postgres
docker inspect -f '{{ .Mounts }}' fm-kafka-ui
docker inspect -f '{{ .Mounts }}' fm-kafka
docker inspect -f '{{ .Mounts }}' fm-zookeeper

docker volume rm {vol-name}
docker volume inspect {vol-name}

volumes

local     4d635284477942c6f2e7cb8f98d27e1386efc86e895c4b3d9d244e8bd71e59ba - zoo
local     31cf7f383ec113a30012711958bd1b133f1e25a7f3bc5a2660117a8b81e0f7cc - zoo
local     86390e3386c0f8c434e66136a203dddd4caa431625a389a44b468ec07e937804 - kafka
local     b6dd62983f892f62f80e5fc82156ded59d76419e3b19fa05aac74ff2a8b0ef25 - kafka
local     e54fe68f01a04d7732ad67cee0c0d7f29369a7be1e2a95655acc0f440a6b5427 - zoo

NETWORK ID     NAME                       DRIVER    SCOPE
docker network inspect 5a979c39c9aa   bridge                     bridge    local
docker network inspect 6f7f9190fd7d   friday-mentoring_default   bridge    local
docker network inspect bfc24d3f68b0   host                       host      local
docker network inspect ea330292897a   none                       null      local
docker network rm ea330292897a   none                       null      local

