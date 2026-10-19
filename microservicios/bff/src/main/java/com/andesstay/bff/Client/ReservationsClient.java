package com.andesstay.bff.Client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class ReservationsClient {

    private final RestClient restClient;

    public ReservationsClient(@Value("${reservations.service.url}") String serviceUrl) {
        this.restClient = RestClient.create(serviceUrl);
    }

    public ResponseEntity<String> forward(HttpMethod method, String path, String authHeader, String body) {
        try {
            RestClient.RequestBodySpec spec = restClient.method(method)
                    .uri(path)
                    .header("Authorization", authHeader != null ? authHeader : "");

            if (body != null && !body.isBlank()) {
                spec.contentType(MediaType.APPLICATION_JSON).body(body);
            }

            ResponseEntity<String> r = spec.retrieve().toEntity(String.class);
            ResponseEntity.BodyBuilder out = ResponseEntity.status(r.getStatusCode());
            if (r.getHeaders().getContentType() != null) {
                out.contentType(r.getHeaders().getContentType());
            }
            return out.body(r.getBody());
        } catch (RestClientResponseException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }
}