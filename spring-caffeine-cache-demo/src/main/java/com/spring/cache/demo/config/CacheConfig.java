package com.spring.cache.demo.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {

        CaffeineCacheManager cacheManager =
                new CaffeineCacheManager("products");

        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .maximumSize(100)
                        .expireAfterWrite(60, TimeUnit.SECONDS)
        );

        return cacheManager;
    }
}