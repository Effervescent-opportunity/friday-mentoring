Годно, но нужно дописывать.

"В лоб" по решению:

> AuthEventRepository оставлен публичным для интеграционных тестов

Отлично.

> CustomJsonSerializer оставлен публичным для работы приложения

Его можно выпилить и сделать реализацию вплоть до такой степени:

```
public void send(String ipAddress, OffsetDateTime time, String userName, SiemEventType eventType) {
    var eventDto = new AuthEventDto(ipAddress, time, userName, eventType);
    var payload = objectMapper.writeValueAsString(eventDto); // << спринговый ObjectMapper
    kafkaTemplate.send(authEventsTopic, payload);
}
```

> Оставила свои ужасные названия, потому что дальше будут еще правки, а придумывать названия я буду еще час

Следующую неделю предлагаю посвятить исправлению печалей и выбору названий, как бы ужасно это ни звучало.

> Кажется, в AuthEventReader нужно возвращать не AuthEventEntity, а какой-нибудь AuthEventDto, но я не уверена

Бывает и такой подход, но overkill.

> При обновлении Spring'а типы в его AuditApplicationEvent могут перестать совпадать с AuthEventType

Именно поэтому в примере задания использованы другие названия :)

> Если в auth_event есть некорректная строчка, то стрим каждый раз будет падать на ней

Всегда можно любыми действиями сломать любой софт.
Последствия можно ограничить и заваливать не весь стрим, а пропускать конкретную строку. Для этого нужно:
1. хранить тип события как String;
2. для преобразования значений туда-сюда использовать парочку `.name()` - `.valueOf()`;
3. поменять логику обработки + вынести try-catch в лямбду

```
stream.forEach(authEventEntity -> {
  try {
    // ...
  } catch (Exception whatever) {
    log.warn("Ор выше гор");
  }
}
```

### Что можно улучшить:

1. Именованные параметры вместо нумерованных
   `@Query(value = "UPDATE AuthEventEntity a SET a.wasSent = true WHERE a.id = ?1")`
   `@Query(value = "UPDATE AuthEventEntity a SET a.wasSent = true WHERE a.id = :id")`

https://stackoverflow.com/questions/10309314

2. Generic именование `public void on(AuditApplicationEvent event)`
можно сделать по аналогии с `ApplicationListener`, то есть:
`public void onAuditApplicationEvent(AuditApplicationEvent event)`

3. Тесты падают на последних версиях Java
```
assertTrue(now.isBefore(dateFromService));
assertEquals(now.getOffset(), dateFromService.getOffset());
assertTrue(ZonedDateTime.now(zoneId).isAfter(dateFromService));
```
Нужно менять на:
```
Assertions.assertThat(dateFromService).isAfterOrEqualTo(now);
Assertions.assertThat(dateFromService.getOffset()).isEqualTo(now.getOffset());
```
4. `KafkaSenderTest` в целом можно выпилить, потому что реализация проверяется интеграционным тестом.
5. Именование пакета `siem.integration.internal` можно сделать короче, т.к. такой уровень вложенности не оправдан.

6. Выглядит ~~кринжово~~ несопровождаемо:

```
    public void sendToSiem() {
        try (Stream<AuthEventEntity> stream = authEventReader.getNotSentEvents()) {
            stream.forEach(authEventEntity -> {
                if (siemSender.send(authEventEntity.getIpAddress(), authEventEntity.getEventTime(),
                        authEventEntity.getUserName(), authEventEntity.getEventType() == AuthEventType.AUTHENTICATION_SUCCESS
                                ? SiemSender.SiemEventType.AUTH_SUCCESS : SiemSender.SiemEventType.AUTH_FAILURE)) {
                    authEventSaver.setSuccessStatus(authEventEntity.getId());
                }
            });
        } catch (Exception ex) {
            LOGGER.warn("Got exception while sending events to SIEM", ex);
        }
    }
```

Можно переписать как-то так:

```
    public void sendToSiem() {
        try (Stream<AuthEventEntity> stream = authEventReader.getNotSentEvents()) {
            stream.forEach(event -> {
                send(event);
                authEventSaver.setSuccessStatus(event.getId());
            });
        } catch (Exception ex) {
            LOGGER.warn("Got exception while sending events to SIEM", ex);
        }
    }

    private void send(AuthEventEntity authEventEntity) {
        var ipAddress = authEventEntity.getIpAddress();
        var eventTime = authEventEntity.getEventTime();
        var userName = authEventEntity.getUserName();
        var eventType = getEventType(authEventEntity);
        siemSender.send(ipAddress, eventTime, userName, eventType);
    }

    private SiemSender.SiemEventType getEventType(AuthEventEntity authEventEntity) {
        return switch (authEventEntity.getEventType()) {
            case AUTHENTICATION_SUCCESS -> SiemSender.SiemEventType.AUTH_SUCCESS;
            case AUTHENTICATION_FAILURE -> SiemSender.SiemEventType.AUTH_FAILURE;
            case AUTHORIZATION_FAILURE -> SiemSender.SiemEventType.AUTH_FAILURE;
        };
    }
```

7. `AuthEventType` - не атрибут `AuthEventEntity` и не должен протекать в слой JPA, это важно.
8. Разделение на `AuthEventReader` и `AuthEventSaver` выглядит избыточно (вайбы CQRS?), можно слить в один интерфейc
9. `AuthEventSaver` и `SiemSender` таки предлагается вынести в пакет `usecase`.
10. По пакетам можно разбросать чуть иначе (btw, насчёт `ClockService` есть сомнения: а не жить ли ему в пакете clock).

```
│   Application.java
│   
├───jpa
│       AuthEventEntity.java
│       AuthEventRepository.java
│       EventRepositoryImpl.java << AuthEventRepositoryFacade (Impl - это что-то на древнем)
│       
├───rest
│       AuthController.java
│       ClockController.java
│       ClockExceptionHandler.java << CustomRestExceptionHandler ?
│       
├───security
│       AuthEventListener.java
│       CustomSecurityConfig.java
│       
├───service
│       ClockService.java
│       
├───siem
│       CustomKafkaConfig.java << KafkaSiemSenderConfiguration ?
│       KafkaSender.java << KafkaSiemSender ?
│       SiemSenderService.java << ScheduledSiemSender и в пакет scheduled ?
│       
└───usecase
        AuthEventReader.java
        AuthEventSaver.java
        SiemSender.java
```