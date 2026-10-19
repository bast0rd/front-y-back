package com.andesstay.admin_kafka.DTO;

public class ConsumerGroupInfoDTO {

    private String groupId;
    private String state;

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}