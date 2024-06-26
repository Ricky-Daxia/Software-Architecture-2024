package org.example.product.Service;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Mono;

import java.time.Duration;

public interface RedisService {

    public Mono<String> getFromCache(String key);

    public Mono<Boolean> putInCache(String key, String value, Duration ttl);

    public Mono<Boolean> deleteFromCache(String key);

}
