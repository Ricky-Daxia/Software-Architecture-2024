package org.example.order.Service;

import org.example.api.Dto.ItemDto;
import org.example.api.Dto.OrderDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.List;

public interface OrderService {

    Mono<Void> insertOrder(long userId, List<ItemDto> items);

    Mono<ResponseEntity<String>> placeOrder(OrderDto orderDto) throws Exception;
}
