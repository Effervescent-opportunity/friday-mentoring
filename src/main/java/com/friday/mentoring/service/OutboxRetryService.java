package com.friday.mentoring.service;

public class OutboxRetryService {


    //
//    @Scheduled(fixedDelayString = "10000")
//    public void retry() {
//        List<OutboxEntity> outboxEntities = outboxRepository.findAllBefore(Instant.now().minusSeconds(60));
//        for (OutboxEntity outbox : outboxEntities) {
//            deliveryMessageQueueService.send(outbox.message());
//            outboxRepository.delete(outbox.id());
//        }
//    }
    //todo make this here


}
