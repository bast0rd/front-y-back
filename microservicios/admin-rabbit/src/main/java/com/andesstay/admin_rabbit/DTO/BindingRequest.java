package com.andesstay.admin_rabbit.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Body de POST /api/admin/rabbit/bindings  (une un exchange con una cola)
 * Ejemplo: { "exchange": "cmd.direct", "queue": "q.cmd.sms", "routingKey": "sms.send" }
 */
public record BindingRequest(
        @NotBlank(message = "El exchange es obligatorio") String exchange,
        @NotBlank(message = "La cola es obligatoria") String queue,
        @NotNull(message = "routingKey es obligatorio (puede ser vacío solo para fanout)") String routingKey) {
}
