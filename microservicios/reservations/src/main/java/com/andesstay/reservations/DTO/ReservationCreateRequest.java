package com.andesstay.reservations.DTO;

import java.time.LocalDateTime;

public class ReservationCreateRequest {
    private String guestId;
    private Long unitId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public ReservationCreateRequest() {}

    public String getGuestId() { return guestId; }
    public void setGuestId(String guestId) { this.guestId = guestId; }

    public Long getUnitId() { return unitId; }
    public void setUnitId(Long unitId) { this.unitId = unitId; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
}