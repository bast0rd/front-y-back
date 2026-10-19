package com.andesstay.notify.Config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topología completa de RabbitMQ para AndesStay.
 *
 * Flujos (3) y sus DLQ:
 *   email        -> q.cmd.email        (DLQ: q.cmd.email.dlq)
 *   housekeeping -> q.cmd.housekeeping (DLQ: q.cmd.housekeeping.dlq)
 *   voucher      -> q.cmd.voucher      (DLQ: q.cmd.voucher.dlq)
 *
 * Rutas:
 *   cmd.direct (direct) --routing key exacta--> cola principal
 *   cmd.topic  (topic)  --patrón--------------> cola principal
 *   cmd.dead.dlx (direct) --nombre de la DLQ--> DLQ   (mensajes rechazados con nack requeue=false)
 *
 * Todos los nombres vienen de MessagingProperties (application.properties).
 */
@Configuration
@EnableConfigurationProperties(MessagingProperties.class)
public class RabbitMQConfig {

    private final MessagingProperties props;

    public RabbitMQConfig(MessagingProperties props) {
        this.props = props;
    }

    // ===================== 1. EXCHANGES =====================

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(props.getExchanges().getDirect(), true, false);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(props.getExchanges().getTopic(), true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(props.getExchanges().getDlx(), true, false);
    }

    // ===================== 2. COLAS PRINCIPALES (con DLX) =====================

    @Bean
    public Queue emailQueue() {
        return mainQueue(props.getQueues().getEmail(), props.getQueues().getEmailDlq());
    }

    @Bean
    public Queue housekeepingQueue() {
        return mainQueue(props.getQueues().getHousekeeping(), props.getQueues().getHousekeepingDlq());
    }

    @Bean
    public Queue voucherQueue() {
        return mainQueue(props.getQueues().getVoucher(), props.getQueues().getVoucherDlq());
    }

    // ===================== 3. DLQ =====================

    @Bean
    public Queue emailDlq() {
        return plainQueue(props.getQueues().getEmailDlq());
    }

    @Bean
    public Queue housekeepingDlq() {
        return plainQueue(props.getQueues().getHousekeepingDlq());
    }

    @Bean
    public Queue voucherDlq() {
        return plainQueue(props.getQueues().getVoucherDlq());
    }

    // ===================== 4. BINDINGS DIRECT =====================

    @Bean
    public Binding emailDirectBinding() {
        return BindingBuilder.bind(emailQueue()).to(directExchange())
                .with(props.getRoutingKeys().getEmailDirect());
    }

    @Bean
    public Binding housekeepingDirectBinding() {
        return BindingBuilder.bind(housekeepingQueue()).to(directExchange())
                .with(props.getRoutingKeys().getHousekeepingDirect());
    }

    @Bean
    public Binding voucherDirectBinding() {
        return BindingBuilder.bind(voucherQueue()).to(directExchange())
                .with(props.getRoutingKeys().getVoucherDirect());
    }

    // ===================== 5. BINDINGS TOPIC =====================

    @Bean
    public Binding emailTopicBinding() {
        return BindingBuilder.bind(emailQueue()).to(topicExchange())
                .with(props.getRoutingKeys().getEmailTopic());
    }

    @Bean
    public Binding housekeepingTopicBinding() {
        return BindingBuilder.bind(housekeepingQueue()).to(topicExchange())
                .with(props.getRoutingKeys().getHousekeepingTopic());
    }

    @Bean
    public Binding voucherTopicBinding() {
        return BindingBuilder.bind(voucherQueue()).to(topicExchange())
                .with(props.getRoutingKeys().getVoucherTopic());
    }

    // ===================== 6. BINDINGS DLQ (los que faltaban) =====================
    // La routing key es el nombre de la DLQ, igual que x-dead-letter-routing-key de la cola principal.

    @Bean
    public Binding emailDlqBinding() {
        return BindingBuilder.bind(emailDlq()).to(deadLetterExchange())
                .with(props.getQueues().getEmailDlq());
    }

    @Bean
    public Binding housekeepingDlqBinding() {
        return BindingBuilder.bind(housekeepingDlq()).to(deadLetterExchange())
                .with(props.getQueues().getHousekeepingDlq());
    }

    @Bean
    public Binding voucherDlqBinding() {
        return BindingBuilder.bind(voucherDlq()).to(deadLetterExchange())
                .with(props.getQueues().getVoucherDlq());
    }

    // ===================== 7. CONVERTER JSON =====================

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // ===================== helpers =====================

    private Queue mainQueue(String name, String dlqName) {
        QueueBuilder builder = QueueBuilder.durable(name)
                .withArgument("x-dead-letter-exchange", props.getExchanges().getDlx())
                .withArgument("x-dead-letter-routing-key", dlqName);
        if (props.isQuorumQueues()) {
            builder.quorum();
        }
        return builder.build();
    }

    private Queue plainQueue(String name) {
        QueueBuilder builder = QueueBuilder.durable(name);
        if (props.isQuorumQueues()) {
            builder.quorum();
        }
        return builder.build();
    }
}
