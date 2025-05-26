package com.example.notiveserver.security

import com.example.notiveserver.service.TokenService
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*

@Component
class JwtTokenProvider(
    @Value("\${authentication.jwt.secret}") private val secretKey: String,
    @Value("\${authentication.jwt.access-expire-length}") private val accessExpireMillis: Long,
    @Value("\${authentication.jwt.refresh-expire-length}") private val refreshExpireMillis: Long,

    private val tokenService: TokenService
) {
    private val key = Keys.hmacShaKeyFor(secretKey.toByteArray())

    private fun createToken(user: CustomUser, expireMillis: Long): String {
        val username = user.username
        val userId = user.getUserId()
        val roles = user.authorities.map { it.authority }

        val now = Date()
        val expiry = Date(now.time + expireMillis)
        return Jwts.builder()
            .setSubject(username)
            .claim("userId", userId)
            .claim("roles", roles)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun createAccessToken(authentication: Authentication): String {
        val user = (authentication.principal as CustomUser)
        return createToken(user, accessExpireMillis)
    }

    fun createRefreshToken(authentication: Authentication): String {
        val user = (authentication.principal as CustomUser)
        val refreshToken = createToken(user, refreshExpireMillis)
        tokenService.saveRefreshToken(
            user.getUserId(),
            refreshToken,
            Duration.ofMillis(refreshExpireMillis)
        )
        return refreshToken
    }

    fun getAuthentication(token: String): Authentication {
        val claims = Jwts.parserBuilder().setSigningKey(key).build()
            .parseClaimsJws(token).body
        val userId = claims["userId"].toString().toLong()
        val authorities = (claims["roles"] as List<*>)
            .map { SimpleGrantedAuthority(it as String) }
        val principal = CustomUser(userId, claims.subject, authorities.map { it.authority })
        return UsernamePasswordAuthenticationToken(principal, token, authorities)
    }

    fun resolveToken(req: HttpServletRequest): String? =
        req.getHeader("Authorization")?.takeIf { it.startsWith("Bearer ") }?.substring(7)

    fun validateToken(token: String): Boolean =
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
            true
        } catch (e: JwtException) {
            false
        }
}
