package com.andesstay.admin_rabbit.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BindingInfoDTO {

    private String source;
    private String destination;
    @JsonProperty("destination_type")
    private String destinationType;
    @JsonProperty("routing_key")
    private String routingKey;
    private String vhost;

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getDestinationType() { return destinationType; }
    public void setDestinationType(String destinationType) { this.destinationType = destinationType; }

    public String getRoutingKey() { return routingKey; }
    public void setRoutingKey(String routingKey) { this.routingKey = routingKey; }

    public String getVhost() { return vhost; }
    public void setVhost(String vhost) { this.vhost = vhost; }
}
