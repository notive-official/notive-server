package com.example.notiveserver.infrastructure.security

import com.example.notiveserver.infrastructure.security.dto.CustomUser
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.*


@Component
class SecurityCurrentUserProvider : CurrentUserProvider {
    private val currentUser: CustomUser
        get() {
            val authentication = SecurityContextHolder.getContext().authentication
            check(!(authentication == null || authentication.principal !is CustomUser)) {
                "인증된 사용자가 없습니다."
            }
            return authentication.principal as CustomUser
        }

    override fun id(): UUID = currentUser.getId()

    override fun username(): String = currentUser.getUsername()

    override fun authorities(): Collection<GrantedAuthority> = currentUser.getAuthorities()

    override fun isAuthenticated(): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication != null &&
                authentication.isAuthenticated &&
                authentication.principal is CustomUser
    }
}