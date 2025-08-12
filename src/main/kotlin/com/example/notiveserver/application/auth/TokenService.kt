package com.example.notiveserver.application.auth

import com.example.notiveserver.infrastructure.security.SecurityUtils
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.access.prepost.PreAuthorize
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

    @PreAuthorize("isAuthenticated()")
    fun saveRefreshToken(token: String, ttl: Duration) {
        val userId = SecurityUtils.currentUserId
        stringRedisTemplate.opsForValue()
            .set(userId.refreshTokenKey, token, ttl)
    }

    @PreAuthorize("isAuthenticated()")
    fun canReissue(oldRefreshToken: String): Boolean {
        val userId = SecurityUtils.currentUserId
        return oldRefreshToken == stringRedisTemplate.opsForValue().get(userId.refreshTokenKey)
    }

    @PreAuthorize("isAuthenticated()")
    fun deleteRefreshToken(): String? {
        val userId = SecurityUtils.currentUserId
        return stringRedisTemplate.opsForValue().getAndDelete(userId.refreshTokenKey)
    }
}
