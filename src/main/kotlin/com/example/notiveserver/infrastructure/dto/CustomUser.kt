package com.example.notiveserver.infrastructure.dto

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUser(
    private val userId: Long,
    private val nickname: String,
    private val roles: List<String>
) : UserDetails {

    fun getUserId(): Long = userId

    override fun getUsername(): String = nickname

    override fun getPassword(): String? = null

    override fun getAuthorities(): Collection<GrantedAuthority> =
        roles.map { SimpleGrantedAuthority(it) }
}
