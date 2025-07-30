package com.example.notiveserver.application.auth

import com.example.notiveserver.domain.repository.UserRepository
import com.example.notiveserver.infrastructure.security.JwtTokenProvider
import com.example.notiveserver.infrastructure.security.dto.CustomUser
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val tokenService: TokenService
) {
    fun reissueAccessToken(refreshToken: String): String {
        val authentication = jwtTokenProvider.getAuthentication(refreshToken)
        val user = (authentication.principal as CustomUser)

        if (!tokenService.canReissue(user.getId(), refreshToken)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 일치하지 않습니다")
        }
        return jwtTokenProvider.createAccessToken(authentication)
    }
}