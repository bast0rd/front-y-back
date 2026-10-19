package com.andesstay.bff.Controller;

import com.andesstay.bff.Client.ReservationsClient;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bff/reservations")
public class ReservationsProxyController {

    private final ReservationsClient reservationsClient;

    public ReservationsProxyController(ReservationsClient reservationsClient) {
        this.reservationsClient = reservationsClient;
    }

    @RequestMapping(value = "/**", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
            RequestMethod.PATCH, RequestMethod.DELETE })
    public ResponseEntity<String> proxy(HttpServletRequest request,
            @RequestBody(required = false) String body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String subPath = request.getRequestURI().substring("/api/bff/reservations".length());
        if (request.getQueryString() != null) {
            subPath = subPath + "?" + request.getQueryString();
        }

        return reservationsClient.forward(HttpMethod.valueOf(request.getMethod()), subPath, authHeader, body);
    }
}