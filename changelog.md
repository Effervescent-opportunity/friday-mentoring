
## Четвёртое задание

- Подняты версии Spring Boot'а до 3.1.2, Spring Dependency Management Plugin'а до 1.1.2
- 

## Третье задание

- Сервис защищен @PreAuthorize, потому что @Secured считается Spring'ом legacy, а использовать @PreFilter - перебор, у меня нет списков для фильтрации.
Можно было бы сделать более сложную логику со специальными бинами или кастомными аннотациями, это тоже перебор относительно требуемого
- Добавлен PKCS12 keystore, команды для генерации ниже. Правда ощущение, что при запуске берется не из него. При старте приложения вот такая фраза в логах
`...o.a.t.util.net.NioEndpoint.certificate   : Connector [...], TLS virtual host [_default_], certificate type [UNDEFINED] configured from [/home/{user}/.keystore] using alias [tomcat]`
- Удален метод logout из AuthController, он был некорректный, надо было использовать SecurityContextLogoutHandler. Теперь по пути `/auth/logout` можно и GET, и POST-запросом делать logout
- Удалена аннотация @EnableWebSecurity, потому что работает и без нее (почему?)

#### Команды для создания pkcs12 keystore
Создать ключ и самоподписанный сертификат: 
`openssl req -x509 -newkey rsa:4096 -keyout FM-key.key -out FM-ss-cert.pem -days 365 -subj "/CN=localhost/"`

Создать PKCS12 keystore:
`openssl pkcs12 -export -in FM-ss-cert.pem -inkey FM-key.key -out FM-keystore.p12`

#### Команды curl
В предыдущих командых нужно заменить http на https и порт на 8443, добавить `-k`, чтобы curl пропускал самоподписанный сертификат.

`curl -k -v -X POST https://localhost:8443/auth/login -H "Content-Type: application/json" -d '{"user": "root", "password": "password"}'`

`curl -k -v https://localhost:8443/time/current/utc -H "Cookie: JSESSIONID=F12CC1A341A11E27B07289E26E540E43"`

`curl -k -v -X POST https://localhost:8443/auth/logout -H "Cookie: JSESSIONID=F12CC1A341A11E27B07289E26E540E43"`

`curl -k -v https://localhost:8443/auth/logout -H "Cookie: JSESSIONID=F12CC1A341A11E27B07289E26E540E43"`

## Второе задание

__Проблема__: у меня не получается по времени - на первое задание ушло около 9 часов суммарного времени, а там все темы так или иначе встречались
на работе или при самообучении. Spring Security - совсем новая для меня тема, плюс по личным причинам 3 дня не было возможности уделять
время обучению. В итоге на это так себе решение ушло 6 часов, и качество сильно хуже. __Можно ли выдавать задание на 2 недели или еще более легкое задание? Или двухступенчатое?__


- OAUTH не нужен, так как его ему нужно внешнее апи + его главная фича - возможность авторизовываться в нескольких местах, мне не надо
- В CustomSecurityConfig для AuthenticationEntryPoint выставлен HTTP код 404, потому что мне понравилась вычитанная идея скрывать страницы под этим кодом, как будто там ничего нет
- Чувствительная информация хранится прямо в коде, работы с header'ами нет по написанной выше причине
- Планировалось хоть как-то шифровать пароль, был выбран bcrypt, потому что он считается дефолтным
- Планировалось разобраться, почему возникает ситуация "я могу 3 раза залогиниться, получить 3 разных JSESSIONID, разлогиниться из одного, и другие останутся рабочими"
- Планировалось разобраться, что такое возможность rememberMe в конфиге, почему везде рекомендуют выключить csrf
- Планировалось разобраться, как exceptionHandling в конфиге соотносится с уже написанным мной ClockExceptionHandler
- Планировалось разобраться, как сделать https

Чтобы авторизоваться, надо отправить POST-запрос с header'ом `Content-Type: application/json` 
и body: `{"user": "root", "password": "password"}`
В header'е ответа придет header Set-Cookie (типа такого Set-Cookie: JSESSIONID=F78B79F2923D5192B59CED17CDC76BF3; Path=/; HttpOnly) с JSESSIONID, и для запросов в ClockController и logout нужно добвлять 
header вида `Cookie: JSESSIONID=F78B79F2923D5192B59CED17CDC76BF3`, значение JSESSIONID из ответа на авторизацию

#### Команды curl
`curl -v -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d '{"user": "root", "password": "password"}'`

`curl -v http://localhost:8080/time/current/utc -H "Cookie: JSESSIONID=F12CC1A341A11E27B07289E26E540E43"`

`curl -v "http://localhost:8080/time/current?timezone=Europe/Paris" -H "Cookie: JSESSIONID=F12CC1A341A11E27B07289E26E540E43"`

## Первое задание

Дополнительное задание не сделано

- Выбран Spring Web, потому что Spring Reactive Web я вообще не знаю
- Java 17 liberica, потому что такая на реальной работе
- Gradle 8.2, потому что он новый и хочется его попробовать
- Alpine linux в Docker, потому что везде написано, что он меньше всего по размеру
- Кривой ClockExceptionHandler, потому что ни один из способов возвращать JSON 
и для строки с ошибкой, и для даты, не сработал, было принято решение оставить как есть
- compose.yaml пустой, потому что времени разобраться и написать уже не остается.
Контейнер именуется случайным образом самим Docker'ом.


