package org.example.product.Mapper;

import org.example.api.Dto.ProductDto;
import org.example.product.Model.Product;
import org.example.product.Repository.ProductImageRepository;
import org.example.product.Service.RedisService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class ProductMapperImpl implements ProductMapper {

    private final ProductImageRepository productImageRepository;

    private final RedisService redisService;

    private final boolean enableRedis = false;

    public ProductMapperImpl(ProductImageRepository productImageRepository,
                             RedisService redisService) {
        this.productImageRepository = productImageRepository;
        this.redisService = redisService;
    }

    private Mono<ProductDto> mapToProductDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setPrice(product.getPrice());
        productDto.setTitle(product.getTitle());
        productDto.setMain_category(product.getMain_category());
        productDto.setAverage_rating(product.getAverage_rating());
        productDto.setParent_asin(product.getParent_asin());
        productDto.setStock(product.getStock());

        if (enableRedis) {
            String key = "url_" + product.getId();
            return redisService.getFromCache(key)
                    .mapNotNull(cachedUrl -> {
                        productDto.setImage_url(cachedUrl);
                        return productDto;
                    })
                    .switchIfEmpty(
                            productImageRepository.findProductImageByProductId(product.getId())
                                    .flatMap(productImage -> {
                                        String url = productImage.getUrl();
                                        return redisService.putInCache(key, url, Duration.ofMinutes(10))
                                                .flatMap(ignored -> {
                                                    productDto.setImage_url(url);
                                                    return Mono.just(productDto);
                                                });
                                    })
                    );
        }

        return productImageRepository.findProductImageByProductId(product.getId())
                .map(productImage -> {
                    productDto.setImage_url(productImage.getUrl());
                    return productDto;
                });
    }

    public Flux<ProductDto> toProductDto(Flux<Product> products) {
        return products.flatMap(this::mapToProductDto);
    }

    public Mono<ProductDto> toProductDto(Mono<Product> product) {
        return product.flatMap(this::mapToProductDto);
    }

}
