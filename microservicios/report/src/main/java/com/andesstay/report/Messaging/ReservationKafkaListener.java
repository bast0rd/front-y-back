package com.andesstay.report.Messaging;

import com.andesstay.report.DTO.ReservationEventKafkaDTO;
import com.andesstay.report.Model.ReservationMetric;
import com.andesstay.report.Repository.ReservationMetricRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationKafkaListener {

    private final ReservationMetricRepository metricRepository;

    @KafkaListener(topics = "reservations.events", groupId = "report-group")
    public void consumeReservationEvent(ReservationEventKafkaDTO event) {
        log.info("Evento consumido desde Kafka en reportes: {}", event);

        LocalDateTime eventTimestamp = event.getTimestamp() != null
                ? LocalDateTime.ofInstant(Instant.ofEpochMilli(event.getTimestamp()), ZoneId.systemDefault())
                : LocalDateTime.now();

        ReservationMetric metric = ReservationMetric.builder()
                .reservationId(event.getReservationId())
                .unitId(event.getUnitId())
                .status(event.getNewStatus())
                .eventTimestamp(eventTimestamp)
                .build();

        metricRepository.save(metric);
    }
}