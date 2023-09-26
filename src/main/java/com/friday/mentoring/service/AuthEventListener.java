package com.friday.mentoring.service;

import com.friday.mentoring.event.repository.AuthEventSaver;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneId;

/**
 * Слушает события аудита (только включенные)
 */
@Component
public class AuthEventListener {

    private final AuthEventSaver authEventSaver;

    public AuthEventListener(AuthEventSaver authEventSaver) {
        this.authEventSaver = authEventSaver;
    }

    @EventListener
    public void on(AuditApplicationEvent event) {
        AuditEvent auditEvent = event.getAuditEvent();

        String ipAddress = "Unknown";
        if (auditEvent.getData().get("details") instanceof WebAuthenticationDetails details) {
            ipAddress = details.getRemoteAddress();
        }

        authEventSaver.save(ipAddress, OffsetDateTime.ofInstant(auditEvent.getTimestamp(), ZoneId.systemDefault()),
                auditEvent.getPrincipal(), AuthEventSaver.AuthEventType.valueOf(auditEvent.getType()));
    }
    /*
    todo read posts and decide smth/ maybe think как будет легче тестировать.
    кто должен маппить тип события, сейвер или листенер? нижний слой ничего не должен знать о верхнем, но кто у меня нижний слой?
    я запуталась. в стаье с хабра нижний слой это энтити, над ними юз кейсы, над ними уже всякие репозитории. но если сейвер это юз кейс
    (считаем, что он мне помог, а не запутал задачу), то этот листенер-получатель событий, он энтити или репозиторий\презентер? чет мне кажется,
    что он энтити, типа бизнес-логика (пусть и спринговая). Значит он нижний слой. И не должен ничего знать о верхнем, то есть о том, как сохраняется
    а значит, что верхний должен сам по строке решить, что это за значение енама.
    Или енам это типа тоже бизнес-логика?

    но интерфейс это про инкапсуляцию = сокрытие реализации. тогда по идее он сам должен енамы разделять.
    но если у меня разное поведение в зависимости от значения енамов,
    да пофиг, все равно не додумаюсь сейчас, пусть интерфейс сам по строке решает, что за енам

    плюс мое чувство прекрасного говорит, пусть интерфейс думает. хотя это не правильно по крайнее мере для кафка сендера ААААа


     */

}
