package com.example.notiveserver.infrastructure.security

import com.example.notiveserver.infrastructure.security.dto.CustomUser
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*


object SecurityUtils {
    private val currentUser: CustomUser
        get() {
            val authentication = SecurityContextHolder.getContext().authentication
            check(!(authentication == null || authentication.principal !is CustomUser)) { "인증된 사용자가 없습니다." }
            return authentication.principal as CustomUser
        }

    val currentUserId: UUID
        get() = currentUser.getId()

    val currentUsername: String
        get() = currentUser.getUsername()

    val currentAuthorities: Collection<GrantedAuthority>
        get() = currentUser.getAuthorities()
}