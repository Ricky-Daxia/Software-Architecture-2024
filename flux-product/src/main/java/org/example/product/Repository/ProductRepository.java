package org.example.product.Repository;

import org.example.product.Model.Product;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends R2dbcRepository<Product, Long> {

    @Query("select * from product limit 10 offset :offset")
    Flux<Product> findProducts(@Param("offset") int offset);

    Mono<Product> findProductsById(long id);

    @Query("select * from product where id = :id for update")
    Mono<Product> findProductsByIdForUpdate(@Param("id") long id);

    @Modifying
    @Query("update product set stock = :stock where id = :id")
    Mono<Void> updateProductById(@Param("id") long id, @Param("stock") int stock);
}
