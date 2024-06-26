package org.example.delivery.Repository;

import org.example.api.Dto.DeliveryItemDto;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository extends R2dbcRepository<DeliveryItemDto, Long> {
}
