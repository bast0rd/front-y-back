package com.andesstay.audit.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "AUDIT_EVENT")
public class AuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "RESERVATION_ID", nullable = false)
    private Long reservationId;

    @Column(name = "EVENT_TYPE", nullable = false)
    private String eventType;

    @Column(name = "PREVIOUS_STATUS")
    private String previousStatus;

    @Column(name = "NEW_STATUS")
    private String newStatus;

    @Column(name = "ACTOR_ID")
    private String actorId;

    @Column(name = "EVENT_TIMESTAMP", nullable = false)
    private LocalDateTime eventTimestamp;

    @Column(name = "TRACE_ID")
    private String traceId;

    @Column(name = "RECORDED_AT", nullable = false)
    private LocalDateTime recordedAt;

    public AuditEvent() {
    }

    @PrePersist
    public void prePersist() {
        this.recordedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(String previousStatus) { this.previousStatus = previousStatus; }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }

    public String getActorId() { return actorId; }
    public void setActorId(String actorId) { this.actorId = actorId; }

    public LocalDateTime getEventTimestamp() { return eventTimestamp; }
    public void setEventTimestamp(LocalDateTime eventTimestamp) { this.eventTimestamp = eventTimestamp; }

    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }

    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }
}