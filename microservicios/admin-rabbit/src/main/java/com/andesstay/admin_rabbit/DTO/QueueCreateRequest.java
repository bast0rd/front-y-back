package com.andesstay.admin_rabbit.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

/**
 * Body de POST /api/admin/rabbit/queues
 * Ejemplo:
 * { "name": "q.cmd.sms", "durable": true, "deadLetterExchange": "cmd.dead.dlx",
 *   "deadLetterRoutingKey": "q.cmd.sms.dlq", "messageTtlMs": 60000 }
 */
public record QueueCreateRequest(
        @NotBlank(message = "El nombre de la cola es obligatorio")
        @Pattern(regexp = NamePatterns.RESOURCE_NAME, message = NamePatterns.RESOURCE_NAME_MESSAGE)
        String name,
        Boolean durable,
        Boolean autoDelete,
        Boolean quorum,
        String deadLetterExchange,
        String deadLetterRoutingKey,
        @Positive(message = "messageTtlMs debe ser mayor que 0")
        Long messageTtlMs) {
}
