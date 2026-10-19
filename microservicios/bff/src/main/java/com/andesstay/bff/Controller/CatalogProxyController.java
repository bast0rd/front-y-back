package com.andesstay.bff.Controller;

import com.andesstay.bff.Client.CatalogClient;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bff/catalog")
public class CatalogProxyController {

    private final CatalogClient catalogClient;

    public CatalogProxyController(CatalogClient catalogClient) {
        this.catalogClient = catalogClient;
    }

    @RequestMapping(value = "/**", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
            RequestMethod.PATCH, RequestMethod.DELETE })
    public ResponseEntity<String> proxy(HttpServletRequest request,
            @RequestBody(required = false) String body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String subPath = request.getRequestURI().substring("/api/bff/catalog".length());
        if (request.getQueryString() != null) {
            subPath = subPath + "?" + request.getQueryString();
        }

        return catalogClient.forward(HttpMethod.valueOf(request.getMethod()), subPath, authHeader, body);
    }
}