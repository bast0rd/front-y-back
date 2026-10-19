package com.andesstay.notify.Service;

import com.andesstay.notify.DTO.EventEnvelopeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class VoucherService {

    private static final Logger log = LoggerFactory.getLogger(VoucherService.class);

    public void generateVoucher(EventEnvelopeDTO<?> event) {
        Map<?, ?> payload = PayloadValidator.requirePayload(event);

        // Aquí iría la generación real del PDF.
        log.info("[VOUCHER] Generando voucher reserva={} huésped={} unidad={} eventId={}",
                payload.get("reservationId"), payload.get("guestId"), payload.get("unitId"), event.getEventId());
    }
}
