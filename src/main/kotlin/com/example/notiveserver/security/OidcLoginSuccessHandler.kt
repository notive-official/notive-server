package com.example.notiveserver.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OidcLoginSuccessHandler : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val oidcUser = authentication.principal as CustomOidcUser

        val customUser = CustomUser(
            userId = oidcUser.getUserId(),
            nickname = oidcUser.getNickname(),
            roles = oidcUser.authorities.map { it.authority }
        )
        val newAuthentication = UsernamePasswordAuthenticationToken(
            customUser,
            authentication.credentials,
            customUser.authorities
        )
        SecurityContextHolder.getContext().authentication = newAuthentication
        request.getRequestDispatcher("/api/auth/login")
            .forward(request, response)
    }
}