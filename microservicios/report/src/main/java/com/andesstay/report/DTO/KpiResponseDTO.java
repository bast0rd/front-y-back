package com.andesstay.report.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KpiResponseDTO {
    private String range;
    private long totalReservations;
    private long activeOccupancy;
    private double avgCycleTimeHours;
    private Map<Integer, Long> reservationsByHour;
}