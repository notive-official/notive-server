package com.example.notiveserver.config

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.cache.RedisCacheWriter
import org.springframework.data.redis.connection.RedisConnectionFactory
import java.time.Duration

@Configuration
@EnableCaching
class CacheConfig {

    @Bean
    fun cacheManager(cf: RedisConnectionFactory): CacheManager {
        // (1) 기본 설정: 캐시별로 기본적으로 5분 TTL
        val defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(5))

        // (2) 특정 캐시에만 다른 TTL 설정
//        val cacheConfigs = mapOf(
//            "refreshTokens" to RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofDays(7))
//        )

        return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(cf))
            .cacheDefaults(defaultConfig)
            // .withInitialCacheConfigurations(cacheConfigs)
            .build()
    }
}