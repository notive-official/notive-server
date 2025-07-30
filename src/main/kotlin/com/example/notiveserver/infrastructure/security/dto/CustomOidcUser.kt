package com.example.notiveserver.infrastructure.security.dto

import com.example.notiveserver.domain.model.user.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import java.util.*

class CustomOidcUser(
    private val user: User,
    private val delegate: OidcUser
) : OidcUser by delegate {

    fun getUserId(): UUID = user.id!!
    fun getNickname(): String = user.nickname

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return user.authorities
            .map { SimpleGrantedAuthority(it.name) }
            .toMutableList()
    }
}
