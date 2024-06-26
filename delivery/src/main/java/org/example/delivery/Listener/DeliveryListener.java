package org.example.delivery.Listener;

import org.example.api.Dto.DeliveryItemDto;
import org.example.delivery.Service.DeliveryService;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DeliveryListener {

    private final DeliveryService deliveryService;

    @Autowired
    public DeliveryListener(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "delivery.queue", durable = "true"),
            exchange = @Exchange(name = "delivery.direct"),
            key = "delivery"
    ))
    public Mono<Void> listen(DeliveryItemDto deliveryItemDto) {
        return deliveryService.saveDeliveryInfo(deliveryItemDto);
    }
}
