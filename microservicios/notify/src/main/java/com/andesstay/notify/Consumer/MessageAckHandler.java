package com.andesstay.notify.Consumer;

import com.andesstay.notify.Config.MessagingProperties;
import com.andesstay.notify.DTO.EventEnvelopeDTO;
import com.andesstay.notify.exception.NonRetryableException;
import com.andesstay.notify.exception.RetryableException;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Política ÚNICA de confirmación de mensajes para todos los consumidores.
 *
 *  - Éxito                      -> basicAck
 *  - Duplicado (mismo eventId)  -> basicAck (idempotencia, no se reprocesa)
 *  - NonRetryableException      -> basicNack(requeue=false) -> DLX -> DLQ   (+ log ERROR)
 *  - RetryableException         -> se reintenta con backoff; si se agotan -> DLQ (+ log ERROR)
 *  - Cualquier otra excepción   -> se trata como no recuperable -> DLQ
 *
 * Nunca se usa requeue=true ante errores: evita bucles infinitos de reentrega.
 */
@Component
public class MessageAckHandler {

    private static final Logger log = LoggerFactory.getLogger(MessageAckHandler.class);
    private static final int MAX_REMEMBERED_EVENTS = 5000;

    @FunctionalInterface
    public interface Task {
        void run() throws Exception;
    }

    private final MessagingProperties props;

    /** Últimos eventId procesados (por cola) para no ejecutar dos veces el mismo evento. */
    private final Set<String> processed = Collections.newSetFromMap(
            Collections.synchronizedMap(new LinkedHashMap<String, Boolean>() {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, Boolean> eldest) {
                    return size() > MAX_REMEMBERED_EVENTS;
                }
            }));

    public MessageAckHandler(MessagingProperties props) {
        this.props = props;
    }

    public void handle(String queue, EventEnvelopeDTO<?> event, Channel channel, long deliveryTag, Task task)
            throws IOException {

        String eventId = (event != null) ? event.getEventId() : null;
        String dedupKey = queue + ":" + eventId;

        if (eventId != null && processed.contains(dedupKey)) {
            log.warn("[{}] Evento duplicado {} ignorado (ack)", queue, eventId);
            channel.basicAck(deliveryTag, false);
            return;
        }

        int maxAttempts = Math.max(1, props.getRetry().getMaxAttempts());
        long waitMs = props.getRetry().getInitialIntervalMs();

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                task.run();
            } catch (NonRetryableException e) {
                reject(queue, event, channel, deliveryTag, "error no recuperable: " + e.getMessage());
                return;
            } catch (RetryableException e) {
                if (attempt == maxAttempts) {
                    reject(queue, event, channel, deliveryTag,
                            "reintentos agotados (" + maxAttempts + "): " + e.getMessage());
                    return;
                }
                log.warn("[{}] Error recuperable evento {} intento {}/{}: {}. Reintento en {} ms",
                        queue, eventId, attempt, maxAttempts, e.getMessage(), waitMs);
                if (!sleep(waitMs)) {
                    channel.basicNack(deliveryTag, false, true); // apagado de la app: se devuelve a la cola
                    return;
                }
                waitMs = (long) (waitMs * props.getRetry().getMultiplier());
                continue;
            } catch (Exception e) {
                reject(queue, event, channel, deliveryTag,
                        "excepción inesperada " + e.getClass().getSimpleName() + ": " + e.getMessage());
                return;
            }

            // Si llegamos aquí la tarea terminó bien
            channel.basicAck(deliveryTag, false);
            if (eventId != null) {
                processed.add(dedupKey);
            }
            log.info("[{}] Evento {} procesado y confirmado (ack)", queue, eventId);
            return;
        }
    }

    /** Rechaza SIN reencolar: RabbitMQ lo enruta al DLX y termina en la DLQ. Queda registrado en logs. */
    private void reject(String queue, EventEnvelopeDTO<?> event, Channel channel, long tag, String reason)
            throws IOException {
        log.error("[{}] Mensaje enviado a DLQ. eventId={} correlationId={} traceId={} motivo={}",
                queue,
                event != null ? event.getEventId() : null,
                event != null ? event.getCorrelationId() : null,
                event != null ? event.getTraceId() : null,
                reason);
        channel.basicNack(tag, false, false);
    }

    private boolean sleep(long ms) {
        try {
            Thread.sleep(ms);
            return true;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
