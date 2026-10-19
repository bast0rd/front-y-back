package com.andesstay.admin_kafka.DTO;

public class TopicInfoDTO {

    private String name;
    private int partitionCount;
    private int replicationFactor;
    private boolean internal;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPartitionCount() { return partitionCount; }
    public void setPartitionCount(int partitionCount) { this.partitionCount = partitionCount; }

    public int getReplicationFactor() { return replicationFactor; }
    public void setReplicationFactor(int replicationFactor) { this.replicationFactor = replicationFactor; }

    public boolean isInternal() { return internal; }
    public void setInternal(boolean internal) { this.internal = internal; }
}