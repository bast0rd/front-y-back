package com.andesstay.reservations.Config;

import com.andesstay.reservations.DTO.ReservationEventMessage;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    private final String bootstrapServers;

    public KafkaProducerConfig(Environment env) {
        this.bootstrapServers = env.getProperty("spring.kafka.bootstrap-servers", "localhost:9092");
    }

    @Bean
    public ProducerFactory<String, ReservationEventMessage> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 5000);

        JacksonJsonSerializer<ReservationEventMessage> valueSerializer = new JacksonJsonSerializer<>();
        valueSerializer.noTypeInfo();

        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), valueSerializer);
    }

    @Bean
    public KafkaTemplate<String, ReservationEventMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}