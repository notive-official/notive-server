package com.example.notiveserver.application.oembed

import com.example.notiveserver.application.oembed.dto.OEmbedInfoDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.Duration

@Repository
class OEmbedCache(
    private val redis: ReactiveRedisOperations<String, OEmbedInfoDto>,
    @Value("\${oembed.cache.ttl-hours:1}") private val ttl: Long
) {

    fun get(url: String): Mono<OEmbedInfoDto> = redis.opsForValue().get(key(url))

    fun put(url: String, info: OEmbedInfoDto): Mono<OEmbedInfoDto> =
        redis.opsForValue().set(key(url), info, Duration.ofHours(ttl))
            .thenReturn(info)

    private fun key(url: String) = "oembed:$url"
}