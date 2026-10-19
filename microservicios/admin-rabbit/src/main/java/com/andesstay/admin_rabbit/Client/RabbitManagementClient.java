package com.andesstay.admin_rabbit.Client;

import com.andesstay.admin_rabbit.DTO.BindingInfoDTO;
import com.andesstay.admin_rabbit.DTO.ExchangeInfoDTO;
import com.andesstay.admin_rabbit.DTO.QueueInfoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Cliente HTTP de bajo nivel contra la Management API de RabbitMQ.
 * Solo habla con RabbitMQ; las reglas de negocio y validaciones viven en RabbitAdminService.
 *
 * IMPORTANTE: los segmentos de la URL se codifican uno a uno. El vhost "/" debe viajar como "%2F".
 */
@Component
public class RabbitManagementClient {

    private final RestClient restClient;
    private final String baseUrl;
    private final String vhost;

    public RabbitManagementClient(@Value("${rabbit.management.url}") String managementUrl,
                                  @Value("${rabbit.management.username}") String username,
                                  @Value("${rabbit.management.password}") String password,
                                  @Value("${rabbit.management.vhost}") String vhost) {
        this.baseUrl = managementUrl.endsWith("/") ? managementUrl.substring(0, managementUrl.length() - 1) : managementUrl;
        this.vhost = vhost;
        this.restClient = RestClient.builder()
                .requestInterceptor(new BasicAuthenticationInterceptor(username, password))
                .build();
    }

    // ---------------- Colas ----------------

    public List<QueueInfoDTO> listQueues() {
        QueueInfoDTO[] queues = restClient.get().uri(uri("api", "queues", vhost))
                .retrieve().body(QueueInfoDTO[].class);
        return queues != null ? List.of(queues) : List.of();
    }

    public QueueInfoDTO getQueue(String name) {
        return restClient.get().uri(uri("api", "queues", vhost, name))
                .retrieve().body(QueueInfoDTO.class);
    }

    public void createQueue(String name, Map<String, Object> body) {
        restClient.put().uri(uri("api", "queues", vhost, name))
                .contentType(MediaType.APPLICATION_JSON).body(body)
                .retrieve().toBodilessEntity();
    }

    public void deleteQueue(String name) {
        restClient.delete().uri(uri("api", "queues", vhost, name))
                .retrieve().toBodilessEntity();
    }

    public void purgeQueue(String name) {
        restClient.delete().uri(uri("api", "queues", vhost, name, "contents"))
                .retrieve().toBodilessEntity();
    }

    // ---------------- Exchanges ----------------

    public List<ExchangeInfoDTO> listExchanges() {
        ExchangeInfoDTO[] exchanges = restClient.get().uri(uri("api", "exchanges", vhost))
                .retrieve().body(ExchangeInfoDTO[].class);
        return exchanges != null ? List.of(exchanges) : List.of();
    }

    public void createExchange(String name, Map<String, Object> body) {
        restClient.put().uri(uri("api", "exchanges", vhost, name))
                .contentType(MediaType.APPLICATION_JSON).body(body)
                .retrieve().toBodilessEntity();
    }

    public void deleteExchange(String name) {
        restClient.delete().uri(uri("api", "exchanges", vhost, name))
                .retrieve().toBodilessEntity();
    }

    // ---------------- Bindings (exchange -> cola) ----------------

    public List<BindingInfoDTO> listBindings() {
        BindingInfoDTO[] bindings = restClient.get().uri(uri("api", "bindings", vhost))
                .retrieve().body(BindingInfoDTO[].class);
        return bindings != null ? List.of(bindings) : List.of();
    }

    public void createBinding(String exchange, String queue, String routingKey) {
        Map<String, Object> body = Map.of("routing_key", routingKey, "arguments", Map.of());
        restClient.post().uri(uri("api", "bindings", vhost, "e", exchange, "q", queue))
                .contentType(MediaType.APPLICATION_JSON).body(body)
                .retrieve().toBodilessEntity();
    }

    public void deleteBinding(String exchange, String queue, String routingKey) {
        // En la Management API, un binding sin argumentos se identifica por su routing key ("~" si es vacía)
        String propertiesKey = routingKey.isEmpty() ? "~" : routingKey;
        restClient.delete().uri(uri("api", "bindings", vhost, "e", exchange, "q", queue, propertiesKey))
                .retrieve().toBodilessEntity();
    }

    // ---------------- util ----------------

    private URI uri(String... segments) {
        StringBuilder sb = new StringBuilder(baseUrl);
        for (String segment : segments) {
            sb.append('/').append(URLEncoder.encode(segment, StandardCharsets.UTF_8).replace("+", "%20"));
        }
        return URI.create(sb.toString());
    }
}
