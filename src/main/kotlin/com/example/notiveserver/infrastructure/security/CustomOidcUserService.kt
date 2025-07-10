package com.example.notiveserver.infrastructure.security

import com.example.notiveserver.common.enums.Role
import com.example.notiveserver.domain.model.user.User
import com.example.notiveserver.domain.repository.AuthorityRepository
import com.example.notiveserver.domain.repository.UserRepository
import com.example.notiveserver.infrastructure.dto.CustomOidcUser
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Service

@Service
class CustomOidcUserService(
    private val userRepository: UserRepository,
    private val authorityRepository: AuthorityRepository
) : OAuth2UserService<OidcUserRequest, OidcUser> {
    private val delegate = OidcUserService()

    override fun loadUser(request: OidcUserRequest): CustomOidcUser {
        val oidcUser = delegate.loadUser(request)

        val registrationId = request.clientRegistration.registrationId

        val oauthId = oidcUser.subject
        val email = oidcUser.email
        val name = oidcUser.fullName
        val socialId = registrationId + oauthId

        val roleUser = authorityRepository.findByName(Role.ROLE_USER.name)
            ?: throw IllegalStateException("ROLE_USER 권한이 존재하지 않습니다.")

        val user = userRepository.findWithAuthoritiesBySocialId(socialId)
            ?.apply { this.name = name }
            ?: User(
                socialId = socialId,
                email = email,
                name = name,
                nickname = name,
                authorities = mutableSetOf(roleUser)
            )
        userRepository.save(user)

        return CustomOidcUser(user, oidcUser)
    }
}
