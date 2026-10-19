package com.andesstay.admin_rabbit.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeInfoDTO {

    private String name;
    private String vhost;
    private String type;
    private Boolean durable;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getVhost() { return vhost; }
    public void setVhost(String vhost) { this.vhost = vhost; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Boolean getDurable() { return durable; }
    public void setDurable(Boolean durable) { this.durable = durable; }
}