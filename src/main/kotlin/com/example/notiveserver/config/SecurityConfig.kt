package com.example.notiveserver.config

import com.example.notiveserver.infrastructure.security.CustomOidcUserService
import com.example.notiveserver.infrastructure.security.JwtAuthenticationFilter
import com.example.notiveserver.infrastructure.security.OidcLoginFailureHandler
import com.example.notiveserver.infrastructure.security.OidcLoginSuccessHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val customOidcUserService: CustomOidcUserService,
    private val loginSuccessHandler: OidcLoginSuccessHandler,
    private val loginFailureHandler: OidcLoginFailureHandler,
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(
        http: HttpSecurity
    ): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .logout { it.disable() }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/**").permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2Login { oauth2 ->
                oauth2
                    .userInfoEndpoint { userInfo ->
                        userInfo.oidcUserService(customOidcUserService)
                    }
                    .successHandler(loginSuccessHandler)
                    .failureHandler(loginFailureHandler)
            }
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java
            )
        return http.build()
    }
}