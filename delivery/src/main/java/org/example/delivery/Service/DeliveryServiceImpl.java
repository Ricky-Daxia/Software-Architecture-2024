package org.example.delivery.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.api.Dto.DeliveryItemDto;
import org.example.api.Utils.JsonConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DeliveryServiceImpl implements DeliveryService {

    private final DatabaseClient databaseClient;

    @Autowired
    public DeliveryServiceImpl(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Void> saveDeliveryInfo(DeliveryItemDto deliveryItemDto) {
        try {
            String jsonData = JsonConverter.toJson(deliveryItemDto.getItems());
            return databaseClient.sql("insert into delivery_info (user_id, items) values (:user_id, :items)")
                    .bind("user_id", deliveryItemDto.getUser_id())
                    .bind("items", jsonData)
                    .then();
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }
}
