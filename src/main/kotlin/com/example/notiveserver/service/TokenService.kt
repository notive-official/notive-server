package com.example.notiveserver.service

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class TokenService(
    private val stringRedisTemplate: StringRedisTemplate
) {
    companion object {
        const val REFRESH_TOKEN_KEY_PREFIX = "refreshToken:"
    }

    private val Long.refreshTokenKey: String
        get() = REFRESH_TOKEN_KEY_PREFIX + this

    fun saveRefreshToken(userId: Long, token: String, ttl: Duration) {
        stringRedisTemplate.opsForValue()
            .set(userId.refreshTokenKey, token, ttl)
    }

    fun canReissue(userId: Long, oldRefreshToken: String): Boolean =
        oldRefreshToken == stringRedisTemplate.opsForValue().get(userId.refreshTokenKey)

    fun deleteRefreshToken(userId: Long): String? =
        stringRedisTemplate.opsForValue().getAndDelete(userId.refreshTokenKey)
}
