
Кроме этого, `getFilteredEntities` уже принимает в себя много параметров и напрашивается их объединение в один объект, то есть:
```
Page<AuthEventEntity> getFilteredEntities(FilterRequest req, Pageable p);

record FilterRequest (
String userName,
String ipAddress,
String eventType,
OffsetDateTime eventTimeFrom,
OffsetDateTime eventTimeTo
) {
// no-op
}
```

