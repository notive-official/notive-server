package com.example.notiveserver.api.controller

import com.example.notiveserver.application.auth.TokenService
import com.example.notiveserver.common.exception.AuthException
import com.example.notiveserver.common.exception.code.AuthErrorCode
import com.example.notiveserver.common.util.CookieUtil
import com.example.notiveserver.infrastructure.dto.CustomUser
import com.example.notiveserver.infrastructure.security.JwtTokenProvider
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val jwtTokenProvider: JwtTokenProvider,
    private val tokenService: TokenService,

    @Value("\${authentication.domain.cookie}") private val cookieDomain: String,
    @Value("\${authentication.url.login-redirect}") private val loginRedirectUrl: String,
) {

    @GetMapping("/login")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun login(response: HttpServletResponse) {
        val authentication = SecurityContextHolder.getContext().authentication
        val refreshToken = jwtTokenProvider.createRefreshToken(authentication)

        CookieUtil.setRefreshTokenCookie(response, refreshToken, cookieDomain)
        response.sendRedirect(loginRedirectUrl)
    }

    @PostMapping("/reissue")
    @ResponseStatus(HttpStatus.OK)
    fun reissue(
        @CookieValue(name = "refreshToken") refreshToken: String,
        response: HttpServletResponse
    ) {
        val authentication = jwtTokenProvider.getAuthentication(refreshToken)
        val userId = (authentication.principal as CustomUser).getId()
        if (!jwtTokenProvider.validateToken(refreshToken) ||
            !tokenService.canReissue(userId, refreshToken)
        ) {
            throw AuthException(AuthErrorCode.CANNOT_REISSUE)
        }
        val accessToken = jwtTokenProvider.createAccessToken(authentication)
        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    fun logout(@AuthenticationPrincipal user: CustomUser, response: HttpServletResponse) {
        tokenService.deleteRefreshToken(user.getId())
        CookieUtil.clearRefreshTokenCookie(response, cookieDomain)
    }
}