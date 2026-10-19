package com.andesstay.reservations.messaging;

import com.andesstay.reservations.Config.RabbitMessagingProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Publica las tareas asíncronas (email, housekeeping, voucher).
 * Si RabbitMQ no está disponible NO se propaga la excepción: la reserva ya quedó guardada y
 * el core del sistema sigue funcionando (mensajería desacoplada).
 */
@Component
public class RabbitPublisher {

    private static final Logger log = LoggerFactory.getLogger(RabbitPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMessagingProperties props;

    public RabbitPublisher(RabbitTemplate rabbitTemplate, RabbitMessagingProperties props) {
        this.rabbitTemplate = rabbitTemplate;
        this.props = props;
    }

    public void sendEmailNotification(Long reservationId, String guestId, String status) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("reservationId", reservationId);
        payload.put("guestId", guestId);
        payload.put("status", status);

        publish(props.getRoutingKeys().getEmailDirect(),
                buildEnvelope("EMAIL_NOTIFICATION", reservationId, payload));
    }

    public void sendHousekeepingTicket(Long reservationId, Long unitId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("reservationId", reservationId);
        payload.put("unitId", unitId);

        publish(props.getRoutingKeys().getHousekeepingDirect(),
                buildEnvelope("HOUSEKEEPING_TICKET", reservationId, payload));
    }

    public void sendVoucherGeneration(Long reservationId, String guestId, Long unitId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("reservationId", reservationId);
        payload.put("guestId", guestId);
        payload.put("unitId", unitId);

        publish(props.getRoutingKeys().getVoucherDirect(),
                buildEnvelope("VOUCHER_GENERATION", reservationId, payload));
    }

    private void publish(String routingKey, Map<String, Object> envelope) {
        try {
            rabbitTemplate.convertAndSend(props.getExchanges().getDirect(), routingKey, envelope);
            log.info("Mensaje publicado exchange={} routingKey={} eventId={}",
                    props.getExchanges().getDirect(), routingKey, envelope.get("eventId"));
        } catch (AmqpException e) {
            log.error("No se pudo publicar en RabbitMQ (routingKey={}, eventId={}): {}. La reserva NO se ve afectada.",
                    routingKey, envelope.get("eventId"), e.getMessage());
        }
    }

    private Map<String, Object> buildEnvelope(String type, Long reservationId, Map<String, Object> payload) {
        Map<String, Object> envelope = new HashMap<>();
        envelope.put("type", type);
        envelope.put("eventId", UUID.randomUUID().toString());
        envelope.put("timestamp", System.currentTimeMillis());
        envelope.put("traceId", UUID.randomUUID().toString());
        envelope.put("correlationId", reservationId.toString());
        envelope.put("payload", payload);
        return envelope;
    }
}
