package com.andesstay.report.DTO;

import lombok.Data;

@Data
public class ReservationEventKafkaDTO {
    private String eventId;
    private String type;
    private Long reservationId;
    private Long unitId;
    private String previousStatus;
    private String newStatus;
    private String actorId;
    private Long timestamp;
    private String traceId;

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

}