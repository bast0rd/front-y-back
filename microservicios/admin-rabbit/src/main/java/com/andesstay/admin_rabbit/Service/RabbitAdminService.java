package com.andesstay.admin_rabbit.Service;

import com.andesstay.admin_rabbit.Client.RabbitManagementClient;
import com.andesstay.admin_rabbit.DTO.BindingInfoDTO;
import com.andesstay.admin_rabbit.DTO.BindingRequest;
import com.andesstay.admin_rabbit.DTO.ExchangeCreateRequest;
import com.andesstay.admin_rabbit.DTO.ExchangeInfoDTO;
import com.andesstay.admin_rabbit.DTO.OperationResultDTO;
import com.andesstay.admin_rabbit.DTO.QueueCreateRequest;
import com.andesstay.admin_rabbit.DTO.QueueInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Toda la lógica de administración de colas, exchanges y bindings.
 * Los controladores solo llaman a estos métodos y no conocen detalles de RabbitMQ.
 */
@Service
public class RabbitAdminService {

    private static final Logger log = LoggerFactory.getLogger(RabbitAdminService.class);

    private final RabbitManagementClient client;

    public RabbitAdminService(RabbitManagementClient client) {
        this.client = client;
    }

    // ===================== Colas =====================

    public List<QueueInfoDTO> listQueues() {
        return client.listQueues();
    }

    public QueueInfoDTO getQueue(String name) {
        return client.getQueue(requireText(name, "name"));
    }

    public OperationResultDTO createQueue(QueueCreateRequest request) {
        boolean durable = request.durable() == null || request.durable();
        boolean autoDelete = request.autoDelete() != null && request.autoDelete();
        boolean quorum = request.quorum() != null && request.quorum();
        boolean hasDlx = hasText(request.deadLetterExchange());
        boolean hasDlRoutingKey = hasText(request.deadLetterRoutingKey());

        if (hasDlRoutingKey && !hasDlx) {
            throw new IllegalArgumentException("deadLetterRoutingKey requiere deadLetterExchange");
        }
        if (quorum && (!durable || autoDelete)) {
            throw new IllegalArgumentException("Una quorum queue debe ser durable y no puede ser auto-delete");
        }

        Map<String, Object> arguments = new LinkedHashMap<>();
        if (quorum) {
            arguments.put("x-queue-type", "quorum");
        }
        if (hasDlx) {
            arguments.put("x-dead-letter-exchange", request.deadLetterExchange().trim());
        }
        if (hasDlRoutingKey) {
            arguments.put("x-dead-letter-routing-key", request.deadLetterRoutingKey().trim());
        }
        if (request.messageTtlMs() != null) {
            arguments.put("x-message-ttl", request.messageTtlMs());
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("durable", durable);
        body.put("auto_delete", autoDelete);
        body.put("arguments", arguments);

        client.createQueue(request.name(), body);
        log.info("Cola creada: {} (durable={}, quorum={}, dlx={})", request.name(), durable, quorum,
                request.deadLetterExchange());
        return new OperationResultDTO("queue", request.name(), "Cola creada correctamente");
    }

    public OperationResultDTO deleteQueue(String name) {
        client.deleteQueue(requireText(name, "name"));
        log.info("Cola eliminada: {}", name);
        return new OperationResultDTO("queue", name, "Cola eliminada correctamente");
    }

    public OperationResultDTO purgeQueue(String name) {
        client.purgeQueue(requireText(name, "name"));
        log.info("Cola vaciada: {}", name);
        return new OperationResultDTO("queue", name, "Mensajes eliminados de la cola");
    }

    // ===================== Exchanges =====================

    public List<ExchangeInfoDTO> listExchanges() {
        return client.listExchanges();
    }

    public OperationResultDTO createExchange(ExchangeCreateRequest request) {
        boolean durable = request.durable() == null || request.durable();
        boolean autoDelete = request.autoDelete() != null && request.autoDelete();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("type", request.type());
        body.put("durable", durable);
        body.put("auto_delete", autoDelete);
        body.put("internal", false);
        body.put("arguments", Map.of());

        client.createExchange(request.name(), body);
        log.info("Exchange creado: {} (type={}, durable={})", request.name(), request.type(), durable);
        return new OperationResultDTO("exchange", request.name(), "Exchange creado correctamente");
    }

    public OperationResultDTO deleteExchange(String name) {
        client.deleteExchange(requireText(name, "name"));
        log.info("Exchange eliminado: {}", name);
        return new OperationResultDTO("exchange", name, "Exchange eliminado correctamente");
    }

    // ===================== Bindings =====================

    public List<BindingInfoDTO> listBindings() {
        return client.listBindings();
    }

    public OperationResultDTO createBinding(BindingRequest request) {
        validateBinding(request.exchange(), request.queue());
        client.createBinding(request.exchange(), request.queue(), request.routingKey());
        log.info("Binding creado: {} -> {} (routingKey='{}')", request.exchange(), request.queue(), request.routingKey());
        return new OperationResultDTO("binding", request.exchange() + " -> " + request.queue(),
                "Binding creado correctamente");
    }

    public OperationResultDTO deleteBinding(String exchange, String queue, String routingKey) {
        validateBinding(exchange, queue);
        String key = routingKey == null ? "" : routingKey;
        client.deleteBinding(exchange, queue, key);
        log.info("Binding eliminado: {} -> {} (routingKey='{}')", exchange, queue, key);
        return new OperationResultDTO("binding", exchange + " -> " + queue, "Binding eliminado correctamente");
    }

    // ===================== helpers =====================

    private void validateBinding(String exchange, String queue) {
        requireText(exchange, "exchange");
        requireText(queue, "queue");
    }

    private String requireText(String value, String field) {
        if (!hasText(value)) {
            throw new IllegalArgumentException("El campo '" + field + "' no puede estar vacío");
        }
        return value.trim();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
