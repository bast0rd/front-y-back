package com.andesstay.audit.messaging;

import com.andesstay.audit.DTO.AuditEventResponse;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AuditTimelinePublisher {

    private static final String TOPIC = "audit.timeline";

    private final KafkaTemplate<String, AuditEventResponse> kafkaTemplate;

    public AuditTimelinePublisher(KafkaTemplate<String, AuditEventResponse> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(AuditEventResponse event) {
        kafkaTemplate.send(TOPIC, event.getReservationId().toString(), event);
    }
}