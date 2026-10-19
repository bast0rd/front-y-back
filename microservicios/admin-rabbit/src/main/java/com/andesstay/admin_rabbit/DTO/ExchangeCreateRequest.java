package com.andesstay.admin_rabbit.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Body de POST /api/admin/rabbit/exchanges
 * Ejemplo: { "name": "cmd.fanout", "type": "fanout", "durable": true }
 */
public record ExchangeCreateRequest(
        @NotBlank(message = "El nombre del exchange es obligatorio")
        @Pattern(regexp = NamePatterns.RESOURCE_NAME, message = NamePatterns.RESOURCE_NAME_MESSAGE)
        String name,
        @NotBlank(message = "El tipo de exchange es obligatorio")
        @Pattern(regexp = "direct|topic|fanout|headers",
                message = "El tipo debe ser uno de: direct, topic, fanout, headers")
        String type,
        Boolean durable,
        Boolean autoDelete) {
}
