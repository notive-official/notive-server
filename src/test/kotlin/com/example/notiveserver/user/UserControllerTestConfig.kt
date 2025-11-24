package com.example.notiveserver.user

import com.example.notiveserver.application.user.AuthService
import com.example.notiveserver.application.user.TokenService
import com.example.notiveserver.application.user.UserService
import com.example.notiveserver.infrastructure.security.JwtTokenProvider
import org.mockito.Mockito.mock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext

@TestConfiguration
class UserControllerTestConfig {
    @Bean
    fun userService(): UserService =
        mock(UserService::class.java)

    @Bean
    fun jwtTokenProvider(): JwtTokenProvider =
        mock(JwtTokenProvider::class.java)

    @Bean
    fun authService(): AuthService =
        mock(AuthService::class.java)

    @Bean
    fun tokenService(): TokenService =
        mock(TokenService::class.java)

    @Bean
    fun jpaMappingContext(): JpaMetamodelMappingContext =
        mock(JpaMetamodelMappingContext::class.java)
}