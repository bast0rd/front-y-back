package com.andesstay.admin_kafka.Service;

import com.andesstay.admin_kafka.DTO.ConsumerGroupInfoDTO;
import com.andesstay.admin_kafka.DTO.TopicInfoDTO;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.admin.TopicListing;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.Collection;

@Service
public class KafkaAdminService {

    private final Admin adminClient;

    public KafkaAdminService(Admin adminClient) {
        this.adminClient = adminClient;
    }

    public List<TopicInfoDTO> listTopics() {
        try {
            Collection<TopicListing> listings = adminClient.listTopics().listings().get();
            Set<String> names = listings.stream().map(TopicListing::name).collect(Collectors.toSet());

            Map<String, TopicDescription> descriptions = adminClient.describeTopics(names).allTopicNames().get();

            return descriptions.values().stream().map(desc -> {
                TopicInfoDTO dto = new TopicInfoDTO();
                dto.setName(desc.name());
                dto.setPartitionCount(desc.partitions().size());
                dto.setReplicationFactor(desc.partitions().isEmpty() ? 0 : desc.partitions().get(0).replicas().size());
                dto.setInternal(desc.isInternal());
                return dto;
            }).collect(Collectors.toList());

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("No se pudo obtener la lista de tópicos de Kafka", e);
        }
    }

    public List<ConsumerGroupInfoDTO> listConsumerGroups() {
        try {
            Collection<ConsumerGroupListing> groups = adminClient.listConsumerGroups().all().get();

            return groups.stream().map(group -> {
                ConsumerGroupInfoDTO dto = new ConsumerGroupInfoDTO();
                dto.setGroupId(group.groupId());
                dto.setState(group.state().map(Enum::name).orElse("UNKNOWN"));
                return dto;
            }).collect(Collectors.toList());

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("No se pudo obtener la lista de consumer groups de Kafka", e);
        }
    }

    public void deleteTopic(String topicName) {
        try {
            adminClient.deleteTopics(List.of(topicName)).all().get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("No se pudo eliminar el tópico " + topicName, e);
        }
    }
}