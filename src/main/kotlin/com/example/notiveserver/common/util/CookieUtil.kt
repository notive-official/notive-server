package com.example.notiveserver.common.util

import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import java.time.Duration

object CookieUtil {

    fun createRefreshTokenCookie(refreshToken: String, domain: String): ResponseCookie {
        return ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(true)
            .domain(domain)
            .path("/")
            .sameSite("Strict")
            .maxAge(Duration.ofDays(7))
            .build()
    }

    fun clearRefreshTokenCookie(response: HttpServletResponse, domain: String) {
        val expiredCookie = ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(true)
            .domain(domain)
            .path("/")
            .maxAge(0)
            .sameSite("Strict")
            .build()
        response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString())
    }
}