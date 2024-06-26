package org.example.product.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class RedisServiceImpl implements RedisService {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Autowired
    public RedisServiceImpl(ReactiveRedisTemplate<String, String> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    @Override
    public Mono<String> getFromCache(String key) {
        return reactiveRedisTemplate.opsForValue().get(key);
    }

    @Override
    public Mono<Boolean> putInCache(String key, String value, Duration ttl) {
        return reactiveRedisTemplate.opsForValue().set(key, value, ttl);
    }


    @Override
    public Mono<Boolean> deleteFromCache(String key) {
        return reactiveRedisTemplate.opsForValue().delete(key);
    }

}
