package com.example.notiveserver.infrastructure.security.dto

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

class CustomUser(
    private val userId: UUID,
    private val nickname: String,
    private val roles: List<String>
) : UserDetails {

    fun getId(): UUID = userId

    override fun getUsername(): String = nickname

    override fun getPassword(): String? = null

    override fun getAuthorities(): Collection<GrantedAuthority> =
        roles.map { SimpleGrantedAuthority(it) }
}
