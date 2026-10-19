package com.andesstay.bff.Controller;

import com.andesstay.bff.Client.ReportClient;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bff/report")
public class ReportProxyController {

    private final ReportClient reportClient;

    public ReportProxyController(ReportClient reportClient) {
        this.reportClient = reportClient;
    }

    @RequestMapping(value = "/**", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
            RequestMethod.PATCH, RequestMethod.DELETE })
    public ResponseEntity<String> proxy(HttpServletRequest request,
            @RequestBody(required = false) String body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String subPath = request.getRequestURI().substring("/api/bff/report".length());
        if (request.getQueryString() != null) {
            subPath = subPath + "?" + request.getQueryString();
        }

        return reportClient.forward(HttpMethod.valueOf(request.getMethod()), subPath, authHeader, body);
    }
}