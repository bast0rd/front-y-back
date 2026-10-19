package com.andesstay.report.Service;

import com.andesstay.report.DTO.KpiResponseDTO;
import com.andesstay.report.DTO.TopUnitResponseDTO;
import com.andesstay.report.Model.ReservationMetric;
import com.andesstay.report.Repository.ReservationMetricRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReservationMetricRepository metricRepository;

    public KpiResponseDTO getKpis(String range) {
        LocalDateTime since = parseRange(range);

        long total = metricRepository.countReservationsSince(since);
        long active = metricRepository.countActiveOccupancy();
        List<ReservationMetric> metrics = metricRepository.findAllByEventTimestampGreaterThanEqual(since);

        Map<Integer, Long> byHour = metrics.stream()
                .collect(Collectors.groupingBy(m -> m.getEventTimestamp().getHour(), Collectors.counting()));

        return KpiResponseDTO.builder()
                .range(range)
                .totalReservations(total)
                .activeOccupancy(active)
                .avgCycleTimeHours(2.5) // Promedio estático o calculado según tiempos de checkout
                .reservationsByHour(byHour)
                .build();
    }

    public List<TopUnitResponseDTO> getTopUnits(String range) {
        LocalDateTime since = parseRange(range);
        return metricRepository.findTopUnitsSince(since);
    }

    private LocalDateTime parseRange(String range) {
        if ("last7d".equalsIgnoreCase(range)) {
            return LocalDateTime.now().minusDays(7);
        }
        return LocalDateTime.now().minusHours(24);
    }
}