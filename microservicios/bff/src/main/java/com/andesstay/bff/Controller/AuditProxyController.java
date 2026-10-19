package com.andesstay.bff.Controller;

import com.andesstay.bff.Client.AuditClient;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bff/audit")
public class AuditProxyController {

    private final AuditClient auditClient;

    public AuditProxyController(AuditClient auditClient) {
        this.auditClient = auditClient;
    }

    @RequestMapping(value = "/**", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
            RequestMethod.PATCH, RequestMethod.DELETE })
    public ResponseEntity<String> proxy(HttpServletRequest request,
            @RequestBody(required = false) String body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String subPath = request.getRequestURI().substring("/api/bff/audit".length());
        if (request.getQueryString() != null) {
            subPath = subPath + "?" + request.getQueryString();
        }

        return auditClient.forward(HttpMethod.valueOf(request.getMethod()), subPath, authHeader, body);
    }
}