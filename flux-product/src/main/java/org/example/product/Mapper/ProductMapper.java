package org.example.product.Mapper;

import org.example.api.Dto.ProductDto;
import org.example.product.Model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductMapper {

    Flux<ProductDto> toProductDto(Flux<Product> productFlux);

    Mono<ProductDto> toProductDto(Mono<Product> productMono);
}
