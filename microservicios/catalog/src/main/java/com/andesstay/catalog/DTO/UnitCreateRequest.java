package com.andesstay.catalog.DTO;

import com.andesstay.catalog.Model.UnitStatus;
import java.math.BigDecimal;

public class UnitCreateRequest {

    private String name;
    private String type;
    private Integer capacity;
    private BigDecimal pricePerNight;
    private UnitStatus status;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public BigDecimal getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }

    public UnitStatus getStatus() { return status; }
    public void setStatus(UnitStatus status) { this.status = status; }
}