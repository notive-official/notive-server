package com.example.notiveserver.service

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class TokenService(
    private val stringRedisTemplate: StringRedisTemplate
) {
    fun saveRefreshToken(userId: Long, token: String, ttl: Duration) {
        stringRedisTemplate.opsForValue()
            .set("refreshToken:$userId", token, ttl)
    }

    fun canReissue(userId: Long, oldRefreshToken: String): Boolean =
        oldRefreshToken == stringRedisTemplate.opsForValue().get("refreshToken:$userId")

    fun deleteRefreshToken(userId: Long): String? =
        stringRedisTemplate.opsForValue().getAndDelete("refreshToken:$userId")

}
