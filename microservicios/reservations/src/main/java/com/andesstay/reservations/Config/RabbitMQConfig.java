package com.andesstay.reservations.Config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * reservations solo PUBLICA. La topología (exchanges, colas, bindings, DLQ) la declara notify al arrancar.
 * Aquí quedan el converter JSON y los nombres de exchange/routing keys leídos desde application.properties.
 */
@Configuration
@EnableConfigurationProperties(RabbitMessagingProperties.class)
public class RabbitMQConfig {

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
