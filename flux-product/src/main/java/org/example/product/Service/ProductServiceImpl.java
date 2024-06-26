package org.example.product.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.product.Model.Product;
import org.example.product.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final RedisService redisService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final boolean enableRedis = false;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, RedisService redisService) {
        this.productRepository = productRepository;
        this.redisService = redisService;
    }

    @Override
    public Flux<Product> getProducts(int page) {
        if (enableRedis) {
            String key = "products_" + page;
            return redisService.getFromCache(key)
                    .flatMapMany(cachedProducts -> Flux.fromIterable(parseProductList((String) cachedProducts)))
                    .switchIfEmpty(
                            productRepository.findProducts(page * 10)
                                    .collectList()
                                    .flatMap(products -> {
                                        String productsAsString = serializeProductList(products);
                                        return redisService.putInCache(key, productsAsString, Duration.ofMinutes(10))
                                                .thenReturn(products);
                                    })
                                    .flatMapMany(Flux::fromIterable)
                    );
        }
        return productRepository.findProducts(page * 10);
    }

    private String serializeProductList(List<Product> products) {
        try {
            return objectMapper.writeValueAsString(products);
        } catch (Exception e) {
            return null;
        }
    }

    private List<Product> parseProductList(String cachedProducts) {
        try {
            return objectMapper.readValue(cachedProducts, new TypeReference<List<Product>>() {
            });
        } catch (Exception e) {
            return getDefaultList();
        }
    }

    private List<Product> getDefaultList() {
        List<Product> defaultList = new ArrayList<>();
        defaultList.add(new Product(1L,
                "Health & Personal Care",
                "iPhone 7 Plus 8 Plus Screen Protector, ZHXIN Transparent Clear Slim Shockproof Scratch Resistant Protective Cover for Apple iPhone 78plus",
                3.8,
                0,
                "B075W927RH",
                0));
        return defaultList;
    }

    @Override
    public Mono<Product> getProductById(long id) {
        if (enableRedis) {
            String key = "product_" + id;
            return redisService.getFromCache(key)
                    .mapNotNull(cachedProduct -> parseProduct((String) cachedProduct))
                    .switchIfEmpty(
                            productRepository.findProductsById(id)
                                    .flatMap(product -> {
                                        String productAsString = serializeProduct(product);
                                        return redisService.putInCache(key, productAsString, Duration.ofMinutes(10))
                                                .thenReturn(product);
                                    })
                    );
        }
        return productRepository.findProductsById(id);
    }

    private String serializeProduct(Product product) {
        try {
            return objectMapper.writeValueAsString(product);
        } catch (Exception e) {
            return null;
        }
    }

    private Product parseProduct(String cachedProduct) {
        try {
            return objectMapper.readValue(cachedProduct, Product.class);
        } catch (Exception e) {
            return new Product(1L,
                    "Health & Personal Care",
                    "iPhone 7 Plus 8 Plus Screen Protector, ZHXIN Transparent Clear Slim Shockproof Scratch Resistant Protective Cover for Apple iPhone 78plus",
                    3.8,
                    0,
                    "B075W927RH",
                    0);
        }
    }

    @Override
    @Transactional
    public Mono<Boolean> updateProductQuantityById(long id, int quantity) {
        if (enableRedis) {
            return productRepository.findProductsByIdForUpdate(id)
                    .flatMap(product -> {
                        if (product.getStock() < quantity) {
                            return Mono.just(false);
                        }
                        String key1 = "products_" + id / 10;
                        String key2 = "product_" + id;
                        return redisService.deleteFromCache(key1).
                                flatMap(ignored -> redisService.deleteFromCache(key2))
                                .flatMap(ignored -> productRepository.updateProductById(id, product.getStock() - quantity)
                                        .then(Mono.just(true)));
                    });
        }
        return productRepository.findProductsByIdForUpdate(id)
                .flatMap(product -> {
                    if (product.getStock() < quantity) {
                        return Mono.just(false);
                    }
                    return productRepository.updateProductById(id, product.getStock() - quantity)
                            .then(Mono.just(true));
                });
    }

}
