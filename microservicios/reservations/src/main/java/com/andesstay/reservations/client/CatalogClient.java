package com.andesstay.reservations.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class CatalogClient {

    private static final Logger log = LoggerFactory.getLogger(CatalogClient.class);

    private final RestClient restClient;
    private final String internalApiKey;

    public CatalogClient(@Value("${catalog.service.url}") String catalogServiceUrl,
                          @Value("${internal.api.key}") String internalApiKey) {
        this.restClient = RestClient.create(catalogServiceUrl);
        this.internalApiKey = internalApiKey;
    }

    public void updateUnitAvailability(Long unitId, String status) {
        try {
            restClient.patch()
                    .uri("/api/catalog/units/{id}/availability", unitId)
                    .header("X-Internal-Key", internalApiKey)
                    .body(Map.of("status", status))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("No se pudo actualizar la disponibilidad de la unidad {} a {}: {}", unitId, status, e.getMessage());
        }
    }
}