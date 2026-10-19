package com.andesstay.reservations.Model;

import java.text.Normalizer;

public enum ReservationStatus {
    CREADA,
    CONFIRMADA,
    CHECKIN_PENDIENTE,
    EN_ESTADIA,
    CHECKOUT,
    CANCELADA;

    public static ReservationStatus fromString(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("El status no puede ser nulo");
        }
        String normalized = Normalizer.normalize(raw.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase();
        return ReservationStatus.valueOf(normalized);
    }
}