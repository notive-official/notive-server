package com.example.notiveserver.api.controller

import com.example.notiveserver.application.auth.AuthService
import com.example.notiveserver.application.auth.TokenService
import com.example.notiveserver.common.exception.AuthException
import com.example.notiveserver.common.exception.code.AuthErrorCode
import com.example.notiveserver.common.util.CookieUtil
import com.example.notiveserver.infrastructure.security.JwtTokenProvider
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val jwtTokenProvider: JwtTokenProvider,
    private val tokenService: TokenService,
    private val authService: AuthService,

    @Value("\${authentication.domain.cookie}") private val cookieDomain: String,
    @Value("\${authentication.url.login-redirect}") private val loginRedirectUrl: String,
) {

    @GetMapping("/login")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun login(response: HttpServletResponse) {
        val refreshToken = jwtTokenProvider.createRefreshToken()

        CookieUtil.setRefreshTokenCookie(response, refreshToken, cookieDomain)
        response.sendRedirect(loginRedirectUrl)
    }

    @PostMapping("/reissue")
    @ResponseStatus(HttpStatus.OK)
    fun reissue(
        @CookieValue(name = "refreshToken") refreshToken: String,
        response: HttpServletResponse
    ) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw AuthException(AuthErrorCode.CANNOT_REISSUE)
        }
        val accessToken = authService.reissueAccessToken(refreshToken)
        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
    }

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse) {
        tokenService.deleteRefreshToken()
        CookieUtil.clearRefreshTokenCookie(response, cookieDomain)
    }
}