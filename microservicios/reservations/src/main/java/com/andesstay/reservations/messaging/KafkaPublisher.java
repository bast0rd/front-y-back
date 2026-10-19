package com.andesstay.reservations.messaging;

import com.andesstay.reservations.DTO.ReservationEventMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class KafkaPublisher {

    private static final String TOPIC = "reservations.events";

    private final KafkaTemplate<String, ReservationEventMessage> kafkaTemplate;

    public KafkaPublisher(KafkaTemplate<String, ReservationEventMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishReservationEvent(Long reservationId, Long unitId, String eventType,
                                         String previousStatus, String newStatus, String actorId) {
        ReservationEventMessage event = new ReservationEventMessage();
        event.setEventId(UUID.randomUUID().toString());
        event.setType(eventType);
        event.setReservationId(reservationId);
        event.setUnitId(unitId);
        event.setPreviousStatus(previousStatus);
        event.setNewStatus(newStatus);
        event.setActorId(actorId);
        event.setTimestamp(System.currentTimeMillis());
        event.setTraceId(UUID.randomUUID().toString());

        kafkaTemplate.send(TOPIC, reservationId.toString(), event);
    }
}