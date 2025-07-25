package com.example.notiveserver.application.auth

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*

@Service
class TokenService(
    private val stringRedisTemplate: StringRedisTemplate
) {
    companion object {
        const val REFRESH_TOKEN_KEY_PREFIX = "refreshToken:"
    }

    private val UUID.refreshTokenKey: String
        get() = REFRESH_TOKEN_KEY_PREFIX + this

    fun saveRefreshToken(userId: UUID, token: String, ttl: Duration) {
        stringRedisTemplate.opsForValue()
            .set(userId.refreshTokenKey, token, ttl)
    }

    fun canReissue(userId: UUID, oldRefreshToken: String): Boolean =
        oldRefreshToken == stringRedisTemplate.opsForValue().get(userId.refreshTokenKey)

    fun deleteRefreshToken(userId: UUID): String? =
        stringRedisTemplate.opsForValue().getAndDelete(userId.refreshTokenKey)
}
