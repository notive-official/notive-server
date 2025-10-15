package com.example.notiveserver.common.util

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.web.util.WebUtils
import java.time.Duration

object CookieUtil {

    private const val REFRESH_TOKEN_HEADER = "refreshToken"

    fun setRefreshTokenCookie(
        response: HttpServletResponse,
        refreshToken: String,
        domain: String
    ) {
        val refreshTokenCookie = ResponseCookie.from(REFRESH_TOKEN_HEADER, refreshToken)
            .httpOnly(true)
            .secure(true)
            .domain(domain)
            .path("/")
            .sameSite("None")
            .maxAge(Duration.ofDays(7))
            .build()
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
    }

    fun clearRefreshTokenCookie(response: HttpServletResponse, domain: String) {
        val expiredCookie = ResponseCookie.from(REFRESH_TOKEN_HEADER, "")
            .httpOnly(true)
            .secure(true)
            .domain(domain)
            .path("/")
            .maxAge(0)
            .sameSite("None")
            .build()
        response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString())
    }

    fun getRefreshTokenCookie(request: HttpServletRequest): String? =
        WebUtils.getCookie(request, REFRESH_TOKEN_HEADER)?.value
}