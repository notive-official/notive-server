package com.example.notiveserver.infrastructure.security

import org.springframework.security.core.GrantedAuthority
import java.util.*

interface CurrentUserProvider {
    fun id(): UUID

    fun username(): String

    fun authorities(): Collection<GrantedAuthority>

    fun isAuthenticated(): Boolean
}