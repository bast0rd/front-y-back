package com.andesstay.notify.Service;

import com.andesstay.notify.DTO.EventEnvelopeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    public void sendReservationEmail(EventEnvelopeDTO<?> event) {
        Map<?, ?> payload = PayloadValidator.requirePayload(event);

        // Aquí iría el envío real (JavaMailSender / push). Si el SMTP falla por timeout:
        //   throw new RetryableException("SMTP no disponible", e);
        log.info("[EMAIL] Notificación a huésped={} reserva={} estado={} eventId={}",
                payload.get("guestId"), payload.get("reservationId"), payload.get("status"), event.getEventId());
    }
}
