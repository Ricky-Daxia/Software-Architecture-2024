package org.example.product.Repository;

import org.example.product.Model.ProductImage;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ProductImageRepository extends R2dbcRepository<ProductImage, Long> {

    Mono<ProductImage> findProductImageByProductId(long product_id);
}
