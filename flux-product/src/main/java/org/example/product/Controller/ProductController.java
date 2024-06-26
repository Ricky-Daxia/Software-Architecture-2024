package org.example.product.Controller;

import org.example.api.Dto.Categories;
import org.example.api.Dto.ItemDto;
import org.example.api.Dto.ProductDto;
import org.example.api.Dto.Setting;
import org.example.product.Mapper.ProductMapper;
import org.example.product.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ProductController {

    private final ProductService productService;

    private final ProductMapper productMapper;

    @Autowired
    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @GetMapping("/products")
    public Flux<ProductDto> getProducts(@RequestParam(defaultValue = "1") int page) {
        return productMapper.toProductDto(productService.getProducts(page));
    }

    @GetMapping("/products/{productId}")
    public Mono<ProductDto> getProductById(@PathVariable long productId) {
        return productMapper.toProductDto(productService.getProductById(productId));
    }

    @GetMapping("/settings")
    public Mono<Setting> getSetting() {
        return Mono.just(new Setting(1, "Standalone Point of Sale", "Store-Pos", "10086", "10087", "15968774896", "", "$", "10", "", "", "d36d"));
    }

    @GetMapping("/categories")
    public Mono<Categories> getCategories() {
        return Mono.just(new Categories("1711853606", "drink", 1711853606));
    }

    @PostMapping("/products/update")
    public Mono<ResponseEntity<String>> updateProducts(@RequestBody ItemDto itemDto) {
        return productService.updateProductQuantityById(itemDto.getId(), itemDto.getQuantity())
                .flatMap(status -> {
                    if (status) {
                        return Mono.just(ResponseEntity.ok().body("ok"));
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("no more stock"));
                });
    }

}

