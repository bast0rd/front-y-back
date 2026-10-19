package com.andesstay.admin_rabbit.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QueueInfoDTO {

    private String name;
    private String vhost;
    private String state;
    private Long messages;
    private Long messages_ready;
    private Long messages_unacknowledged;
    private Integer consumers;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getVhost() { return vhost; }
    public void setVhost(String vhost) { this.vhost = vhost; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public Long getMessages() { return messages; }
    public void setMessages(Long messages) { this.messages = messages; }

    public Long getMessages_ready() { return messages_ready; }
    public void setMessages_ready(Long messages_ready) { this.messages_ready = messages_ready; }

    public Long getMessages_unacknowledged() { return messages_unacknowledged; }
    public void setMessages_unacknowledged(Long messages_unacknowledged) { this.messages_unacknowledged = messages_unacknowledged; }

    public Integer getConsumers() { return consumers; }
    public void setConsumers(Integer consumers) { this.consumers = consumers; }
}