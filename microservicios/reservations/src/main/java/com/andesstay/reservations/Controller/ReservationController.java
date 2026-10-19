package com.andesstay.reservations.Controller;

import com.andesstay.reservations.DTO.ReservationCreateRequest;
import com.andesstay.reservations.DTO.ReservationResponse;
import com.andesstay.reservations.DTO.ReservationStatusUpdateRequest;
import com.andesstay.reservations.Model.ReservationStatus;
import com.andesstay.reservations.service.ReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody ReservationCreateRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        if (isClienteOnly(jwt)) {
            request.setGuestId(extractIdentity(jwt));
        }

        ReservationResponse response = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservationById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        ReservationResponse response = reservationService.getReservationById(id);

        if (isClienteOnly(jwt) && !extractIdentity(jwt).equals(response.getGuestId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations(
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @AuthenticationPrincipal Jwt jwt) {

        String guestIdFilter = isClienteOnly(jwt) ? extractIdentity(jwt) : null;

        List<ReservationResponse> response = reservationService.getAllReservations(status, from, to, guestIdFilter);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ReservationResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody ReservationStatusUpdateRequest request) {
        ReservationStatus status = ReservationStatus.fromString(request.getStatus());
        ReservationResponse response = reservationService.updateStatus(id, status);
        return ResponseEntity.ok(response);
    }

    private boolean isClienteOnly(Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("roles");
        if (roles == null) {
            return false;
        }
        return roles.contains("Cliente") && !roles.contains("Admin") && !roles.contains("Operador");
    }

    private String extractIdentity(Jwt jwt) {
        String preferred = jwt.getClaimAsString("preferred_username");
        return preferred != null ? preferred : jwt.getSubject();
    }
}