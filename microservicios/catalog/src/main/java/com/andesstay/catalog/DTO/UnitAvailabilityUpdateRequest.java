package com.andesstay.catalog.DTO;

import com.andesstay.catalog.Model.UnitStatus;

public class UnitAvailabilityUpdateRequest {
    private UnitStatus status;

    public UnitStatus getStatus() { return status; }
    public void setStatus(UnitStatus status) { this.status = status; }
}