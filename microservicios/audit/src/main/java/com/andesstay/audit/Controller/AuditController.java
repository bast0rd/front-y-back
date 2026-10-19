package com.andesstay.audit.Controller;

import com.andesstay.audit.DTO.AuditEventResponse;
import com.andesstay.audit.Service.AuditService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping("/events")
    public ResponseEntity<List<AuditEventResponse>> getEvents(
            @RequestParam(required = false) String actorId,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        List<AuditEventResponse> response = auditService.getEvents(actorId, eventType, from, to);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reservations/{id}")
    public ResponseEntity<List<AuditEventResponse>> getTimelineByReservation(@PathVariable Long id) {
        List<AuditEventResponse> response = auditService.getTimelineByReservation(id);
        return ResponseEntity.ok(response);
    }
}