package org.example.delivery.Service;

import org.example.api.Dto.DeliveryItemDto;
import reactor.core.publisher.Mono;

public interface DeliveryService {

    public Mono<Void> saveDeliveryInfo(DeliveryItemDto deliveryItemDto);
}
