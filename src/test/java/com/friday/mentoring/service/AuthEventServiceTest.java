package com.friday.mentoring.service;

import com.friday.mentoring.db.repository.AuthEventRepository;
import com.friday.mentoring.db.repository.OutboxRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionTemplate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthEventServiceTest {//todo write
//todo что значит оценить покрытие и реалистичность проверки таких тестов - к каким кейсам устойчивы, к каким - неустойчивы,
    // что значит тесты неустойчивы к кейсам. входные данные изменятся и тесты не покажут ошибку? хз, мне кажется при норм написанных
    //тестах они покроют все
    //юнит тестами не протестировать всякие аспекты, event'ы, контексты, транзакции - сам Спринг, например. или flyway
    //что при попытке отправить запрос не с тем паролем будет 404 (или не оно, не помню), и создастся event с неуспешной аутентификацией
    //кажется я не настолько тупая. или это задания легкие?

    @Spy
    AuthEventRepository authEventRepository;
    @Spy
    OutboxRepository outboxRepository;
    @Mock
    TransactionTemplate transactionTemplate;
    @Mock
    KafkaProducer kafkaProducer;

    @InjectMocks
    AuthEventService authEventService;

    @Test
    public void test1() {
        authEventService.processEvent(null);
    }

}