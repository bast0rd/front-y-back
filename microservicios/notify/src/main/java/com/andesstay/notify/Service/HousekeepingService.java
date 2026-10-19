package com.andesstay.notify.Service;

import com.andesstay.notify.DTO.EventEnvelopeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class HousekeepingService {

    private static final Logger log = LoggerFactory.getLogger(HousekeepingService.class);

    public void createTicket(EventEnvelopeDTO<?> event) {
        Map<?, ?> payload = PayloadValidator.requirePayload(event);

        if (payload.get("unitId") == null) {
            throw new com.andesstay.notify.exception.NonRetryableException("unitId es obligatorio para el ticket");
        }
        log.info("[HOUSEKEEPING] Ticket de preparación unidad={} reserva={} eventId={}",
                payload.get("unitId"), payload.get("reservationId"), event.getEventId());
    }
}
