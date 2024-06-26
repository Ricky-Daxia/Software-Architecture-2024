package org.example.product.Service;

import org.example.product.Model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    Flux<Product> getProducts(int page);

    Mono<Product> getProductById(long id);

    Mono<Boolean> updateProductQuantityById(long id, int quantity);
}
