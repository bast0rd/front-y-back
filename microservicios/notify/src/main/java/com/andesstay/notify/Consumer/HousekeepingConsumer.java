package com.andesstay.notify.Consumer;

import com.andesstay.notify.DTO.EventEnvelopeDTO;
import com.andesstay.notify.Service.HousekeepingService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Consumidor del dominio "housekeeping". El ack/nack se hace de forma explícita a través de MessageAckHandler
 * (ack si sale bien; nack sin requeue -> DLQ si falla).
 */
@Component
public class HousekeepingConsumer {

    private final HousekeepingService service;
    private final MessageAckHandler ackHandler;
    private final String queueName;

    public HousekeepingConsumer(HousekeepingService service,
            MessageAckHandler ackHandler,
            @Value("${app.rabbit.queues.housekeeping}") String queueName) {
        this.service = service;
        this.ackHandler = ackHandler;
        this.queueName = queueName;
    }

    @RabbitListener(queues = "${app.rabbit.queues.housekeeping}")
    public void receive(EventEnvelopeDTO<?> event,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        ackHandler.handle(queueName, event, channel, deliveryTag, () -> service.createTicket(event));
    }
}
