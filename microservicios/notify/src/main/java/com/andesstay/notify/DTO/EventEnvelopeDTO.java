package com.andesstay.notify.DTO;

import java.io.Serializable;

public class EventEnvelopeDTO<T> implements Serializable {

    private String type;
    private String eventId;
    private Long timestamp;
    private String traceId;
    private String correlationId;
    private T payload;

    public EventEnvelopeDTO() {}

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }

    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

    public T getPayload() { return payload; }
    public void setPayload(T payload) { this.payload = payload; }
}