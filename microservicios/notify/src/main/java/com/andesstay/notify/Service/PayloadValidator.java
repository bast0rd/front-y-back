package com.andesstay.notify.Service;

import com.andesstay.notify.DTO.EventEnvelopeDTO;
import com.andesstay.notify.exception.NonRetryableException;
import com.andesstay.notify.exception.RetryableException;

import java.util.Map;

/**
 * Valida el payload del envelope. Incluye dos "interruptores" para DEMOSTRAR la DLQ en la presentación:
 *   "simulateFailure": true           -> error no recuperable (va directo a la DLQ)
 *   "simulateTransientFailure": true  -> error recuperable (reintenta N veces y luego va a la DLQ)
 */
public final class PayloadValidator {

    private PayloadValidator() {
    }

    public static Map<?, ?> requirePayload(EventEnvelopeDTO<?> event) {
        if (event == null || !(event.getPayload() instanceof Map<?, ?> payload)) {
            throw new NonRetryableException("Payload inválido o ausente");
        }
        if (Boolean.TRUE.equals(payload.get("simulateFailure"))) {
            throw new NonRetryableException("Falla simulada (demo DLQ)");
        }
        if (Boolean.TRUE.equals(payload.get("simulateTransientFailure"))) {
            throw new RetryableException("Falla transitoria simulada (demo reintentos)");
        }
        if (payload.get("reservationId") == null) {
            throw new NonRetryableException("reservationId es obligatorio");
        }
        return payload;
    }
}
