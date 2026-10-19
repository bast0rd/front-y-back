package com.andesstay.reservations.service;

import com.andesstay.reservations.DTO.ReservationCreateRequest;
import com.andesstay.reservations.DTO.ReservationResponse;
import com.andesstay.reservations.Model.Reservation;
import com.andesstay.reservations.Model.ReservationStatus;
import com.andesstay.reservations.Repository.ReservationRepository;
import com.andesstay.reservations.client.CatalogClient;
import com.andesstay.reservations.messaging.KafkaPublisher;
import com.andesstay.reservations.messaging.RabbitPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationRepository reservationRepository;
    private final RabbitPublisher rabbitPublisher;
    private final KafkaPublisher kafkaPublisher;
    private final CatalogClient catalogClient;

    public ReservationService(ReservationRepository reservationRepository,
            RabbitPublisher rabbitPublisher,
            KafkaPublisher kafkaPublisher,
            CatalogClient catalogClient) {
        this.reservationRepository = reservationRepository;
        this.rabbitPublisher = rabbitPublisher;
        this.kafkaPublisher = kafkaPublisher;
        this.catalogClient = catalogClient;
    }

    public List<ReservationResponse> getAllReservations(ReservationStatus status, LocalDateTime from,
            LocalDateTime to, String guestIdFilter) {
        List<Reservation> reservations;
        if (status != null && from != null && to != null) {
            reservations = reservationRepository.findByStatusAndStartDateBetween(status, from, to);
        } else if (status != null) {
            reservations = reservationRepository.findByStatus(status);
        } else if (from != null && to != null) {
            reservations = reservationRepository.findByStartDateBetween(from, to);
        } else {
            reservations = reservationRepository.findAll();
        }

        if (guestIdFilter != null) {
            reservations = reservations.stream()
                    .filter(r -> guestIdFilter.equals(r.getGuestId()))
                    .collect(Collectors.toList());
        }

        return reservations.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public ReservationResponse getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));
        return mapToResponse(reservation);
    }

    @Transactional
    public ReservationResponse createReservation(ReservationCreateRequest request) {
        Reservation reservation = new Reservation();
        reservation.setGuestId(request.getGuestId());
        reservation.setUnitId(request.getUnitId());
        reservation.setStartDate(request.getStartDate());
        reservation.setEndDate(request.getEndDate());
        reservation.setStatus(ReservationStatus.CREADA);
        reservation.setCreatedAt(LocalDateTime.now());

        Reservation saved = reservationRepository.save(reservation);

        try {
            kafkaPublisher.publishReservationEvent(saved.getId(), saved.getUnitId(), "RESERVATION_CREATED",
                    null, saved.getStatus().name(), getCurrentActorId());
        } catch (Exception e) {
            log.warn("No se pudo publicar el evento de creación en Kafka para la reserva {}: {}", saved.getId(), e.getMessage());
        }

        return mapToResponse(saved);
    }

    @Transactional
    public ReservationResponse updateStatus(Long id, ReservationStatus newStatus) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));

        ReservationStatus currentStatus = reservation.getStatus();

        if ((newStatus == ReservationStatus.EN_ESTADIA || newStatus == ReservationStatus.CHECKIN_PENDIENTE)
                && currentStatus != ReservationStatus.CONFIRMADA
                && currentStatus != ReservationStatus.CHECKIN_PENDIENTE) {
            throw new IllegalStateException(
                    "Transición no permitida: No se puede hacer check-in si la reserva no está CONFIRMADA.");
        }

        reservation.setStatus(newStatus);
        Reservation updated = reservationRepository.save(reservation);

        if (newStatus == ReservationStatus.CONFIRMADA) {
            catalogClient.updateUnitAvailability(updated.getUnitId(), "OCUPADA");
            rabbitPublisher.sendEmailNotification(updated.getId(), updated.getGuestId(), newStatus.name());
            rabbitPublisher.sendHousekeepingTicket(updated.getId(), updated.getUnitId());
            rabbitPublisher.sendVoucherGeneration(updated.getId(), updated.getGuestId(), updated.getUnitId());
        } else if (newStatus == ReservationStatus.CHECKOUT || newStatus == ReservationStatus.CANCELADA) {
            catalogClient.updateUnitAvailability(updated.getUnitId(), "DISPONIBLE");
        }

        try {
            kafkaPublisher.publishReservationEvent(updated.getId(), updated.getUnitId(), "STATUS_UPDATED",
                    currentStatus.name(), newStatus.name(), getCurrentActorId());
        } catch (Exception e) {
            log.warn("No se pudo publicar el evento de cambio de estado en Kafka para la reserva {}: {}", updated.getId(), e.getMessage());
        }

        return mapToResponse(updated);
    }

    private ReservationResponse mapToResponse(Reservation reservation) {
        ReservationResponse response = new ReservationResponse();
        response.setId(reservation.getId());
        response.setGuestId(reservation.getGuestId());
        response.setUnitId(reservation.getUnitId());
        response.setStatus(reservation.getStatus());
        response.setStartDate(reservation.getStartDate());
        response.setEndDate(reservation.getEndDate());
        response.setCreatedAt(reservation.getCreatedAt());
        return response;
    }

    private String getCurrentActorId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Jwt jwt) {
            String preferred = jwt.getClaimAsString("preferred_username");
            return preferred != null ? preferred : jwt.getSubject();
        }
        return "system";
    }
}