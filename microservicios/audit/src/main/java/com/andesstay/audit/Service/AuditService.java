package com.andesstay.audit.Service;

import com.andesstay.audit.DTO.AuditEventResponse;
import com.andesstay.audit.DTO.ReservationEventDTO;
import com.andesstay.audit.Model.AuditEvent;
import com.andesstay.audit.Repository.AuditEventRepository;
import com.andesstay.audit.messaging.AuditTimelinePublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditService {

    private final AuditEventRepository auditEventRepository;
    private final AuditTimelinePublisher auditTimelinePublisher;

    public AuditService(AuditEventRepository auditEventRepository, AuditTimelinePublisher auditTimelinePublisher) {
        this.auditEventRepository = auditEventRepository;
        this.auditTimelinePublisher = auditTimelinePublisher;
    }

    @Transactional
    public void registerEvent(ReservationEventDTO event) {
        AuditEvent auditEvent = new AuditEvent();
        auditEvent.setReservationId(event.getReservationId());
        auditEvent.setEventType(event.getType());
        auditEvent.setPreviousStatus(event.getPreviousStatus());
        auditEvent.setNewStatus(event.getNewStatus());
        auditEvent.setActorId(event.getActorId());
        auditEvent.setTraceId(event.getTraceId());

        LocalDateTime timestamp = event.getTimestamp() != null
                ? LocalDateTime.ofInstant(Instant.ofEpochMilli(event.getTimestamp()), ZoneId.systemDefault())
                : LocalDateTime.now();
        auditEvent.setEventTimestamp(timestamp);

        AuditEvent saved = auditEventRepository.save(auditEvent);

        auditTimelinePublisher.publish(mapToResponse(saved));
    }

    public List<AuditEventResponse> getTimelineByReservation(Long reservationId) {
        return auditEventRepository.findByReservationIdOrderByEventTimestampAsc(reservationId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<AuditEventResponse> getEvents(String actorId, String eventType, LocalDateTime from, LocalDateTime to) {
        List<AuditEvent> events;

        if (actorId != null && from != null && to != null) {
            events = auditEventRepository.findByActorIdAndEventTimestampBetween(actorId, from, to);
        } else if (eventType != null && from != null && to != null) {
            events = auditEventRepository.findByEventTypeAndEventTimestampBetween(eventType, from, to);
        } else if (actorId != null) {
            events = auditEventRepository.findByActorId(actorId);
        } else if (eventType != null) {
            events = auditEventRepository.findByEventType(eventType);
        } else if (from != null && to != null) {
            events = auditEventRepository.findByEventTimestampBetween(from, to);
        } else {
            events = auditEventRepository.findAll();
        }

        return events.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private AuditEventResponse mapToResponse(AuditEvent event) {
        AuditEventResponse response = new AuditEventResponse();
        response.setId(event.getId());
        response.setReservationId(event.getReservationId());
        response.setEventType(event.getEventType());
        response.setPreviousStatus(event.getPreviousStatus());
        response.setNewStatus(event.getNewStatus());
        response.setActorId(event.getActorId());
        response.setEventTimestamp(event.getEventTimestamp());
        response.setTraceId(event.getTraceId());
        return response;
    }
}