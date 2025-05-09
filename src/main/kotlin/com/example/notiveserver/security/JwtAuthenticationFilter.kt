package com.example.notiveserver.security

import com.example.notiveserver.service.AuthService
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.WebUtils

@Component
class JwtAuthenticationFilter(
    private val jwtProvider: JwtTokenProvider,
    private val authService: AuthService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            jwtProvider.resolveToken(request)?.let { token ->
                val auth = jwtProvider.getAuthentication(token)
                SecurityContextHolder.getContext().authentication = auth
            }
        } catch (ex: ExpiredJwtException) {
            val refreshToken = WebUtils.getCookie(request, "refreshToken")?.value
            if (refreshToken != null && jwtProvider.validateToken(refreshToken)) {
                val accessToken = authService.reissueAccessToken(refreshToken)
                response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                val newAuth = jwtProvider.getAuthentication(accessToken)
                SecurityContextHolder.getContext().authentication = newAuth
            }
        }
        filterChain.doFilter(request, response)
    }
}