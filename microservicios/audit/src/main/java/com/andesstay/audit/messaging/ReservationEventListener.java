package com.andesstay.audit.messaging;

import com.andesstay.audit.DTO.ReservationEventDTO;
import com.andesstay.audit.Service.AuditService;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ReservationEventListener {
    private final AuditService auditService;

    public ReservationEventListener(AuditService auditService) {
        this.auditService = auditService;
    }

    @KafkaListener(topics = "reservations.events", groupId = "audit-service")
    public void onReservationEvent(ReservationEventDTO event) {
        auditService.registerEvent(event);
    }
}