package com.andesstay.audit.Repository;

import com.andesstay.audit.Model.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {

    List<AuditEvent> findByReservationIdOrderByEventTimestampAsc(Long reservationId);

    List<AuditEvent> findByActorId(String actorId);

    List<AuditEvent> findByEventType(String eventType);

    List<AuditEvent> findByEventTimestampBetween(LocalDateTime from, LocalDateTime to);

    List<AuditEvent> findByActorIdAndEventTimestampBetween(String actorId, LocalDateTime from, LocalDateTime to);

    List<AuditEvent> findByEventTypeAndEventTimestampBetween(String eventType, LocalDateTime from, LocalDateTime to);
}