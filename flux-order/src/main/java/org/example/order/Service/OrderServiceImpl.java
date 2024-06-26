package org.example.order.Service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.example.api.Dto.DeliveryItemDto;
import org.example.api.Dto.ItemDto;
import org.example.api.Dto.OrderDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final DatabaseClient databaseClient;

    private final WebClient.Builder webClientBuilder;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public OrderServiceImpl(DatabaseClient databaseClient,
                            WebClient.Builder webClientBuilder,
                            RabbitTemplate rabbitTemplate) {
        this.databaseClient = databaseClient;
        this.webClientBuilder = webClientBuilder;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Mono<Void> insertOrder(long userId, List<ItemDto> items) {
        return databaseClient.sql("insert into orders (user_id) values (:user_id)")
                .bind("user_id", userId)
                .fetch()
                .rowsUpdated()
                .flatMap(rowsUpdated -> databaseClient.sql("select LAST_INSERT_ID() as order_id")
                        .map(row -> row.get("order_id", Long.class))
                        .one()
                        .flatMap(orderId -> {
                            Flux<Void> insertItemFlux = Flux.fromIterable(items)
                                    .flatMap(item -> databaseClient.sql("insert into order_item (order_id, product_id, quantity) values (?, ?, ?)")
                                            .bind(0, orderId)
                                            .bind(1, item.getId())
                                            .bind(2, item.getQuantity())
                                            .fetch()
                                            .rowsUpdated()
                                            .then());
                            return insertItemFlux.then();
                        }));
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "placeOrder", fallbackMethod = "handle")
    public Mono<ResponseEntity<String>> placeOrder(OrderDto orderDto) throws Exception {
        List<Mono<ResponseEntity<String>>> responses = new ArrayList<>();
        var items = orderDto.getItems();
        WebClient webClient = webClientBuilder
                .baseUrl("http://flux-product/products")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        for (ItemDto item : items) {
            Mono<ResponseEntity<String>> response =
                    webClient.post()
                            .uri("/update")
                            .bodyValue(item)
                            .retrieve()
                            .toEntity(String.class)
                            .onErrorResume(e -> {
                                System.out.println(e.getMessage());
                                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()));
                            });
            responses.add(response);
        }
        return Flux.concat(responses)
                .index()
                .collectList()
                .flatMap(responsesWithIndexes -> {
                    List<Long> failIndexes = new ArrayList<>();
                    List<ItemDto> updatedItems = new ArrayList<>();
                    for (int i = 0; i < responsesWithIndexes.size(); i++) {
                        if (responsesWithIndexes.get(i).getT2().getStatusCode().is2xxSuccessful()) {
                            updatedItems.add(items.get(i));
                        } else {
                            failIndexes.add((long) i);
                        }
                    }
                    if (failIndexes.isEmpty()) {
                        rabbitTemplate.convertAndSend("delivery.direct", "delivery",
                                new DeliveryItemDto(orderDto.getUser_id(), updatedItems));
                        return insertOrder(orderDto.getUser_id(), updatedItems)
                                .then(Mono.just(ResponseEntity.ok().body("success")));
                    }
                    StringBuilder failedMessage = new StringBuilder("Failed at indexes: ");
                    for (int i = 0; i < failIndexes.size(); i++) {
                        failedMessage.append(failIndexes.get(i));
                        if (i < failIndexes.size() - 1) {
                            failedMessage.append(", ");
                        }
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(failedMessage.toString()));
                });
    }

    private Mono<ResponseEntity<String>> handle(Throwable throwable) {
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("please try again"));
    }

}
