package com.example.notiveserver.application.auth

import com.example.notiveserver.infrastructure.security.SecurityCurrentUserProvider
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*

@Service
class TokenService(
    private val stringRedisTemplate: StringRedisTemplate,
    private val currentUser: SecurityCurrentUserProvider
) {
    companion object {
        const val REFRESH_TOKEN_KEY_PREFIX = "refreshToken:"
    }

    private val UUID.refreshTokenKey: String
        get() = REFRESH_TOKEN_KEY_PREFIX + this

    @PreAuthorize("isAuthenticated()")
    fun saveRefreshToken(token: String, ttl: Duration) {
        val userId = currentUser.id()
        stringRedisTemplate.opsForValue()
            .set(userId.refreshTokenKey, token, ttl)
    }

    fun canReissue(userId: UUID, oldRefreshToken: String): Boolean =
        oldRefreshToken == stringRedisTemplate.opsForValue().get(userId.refreshTokenKey)

    @PreAuthorize("isAuthenticated()")
    fun deleteRefreshToken(): String? {
        val userId = currentUser.id()
        return stringRedisTemplate.opsForValue().getAndDelete(userId.refreshTokenKey)
    }
}
