package com.example.notiveserver.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
@EnableConfigurationProperties(CorsConfig::class)
@ConfigurationProperties(prefix = "authentication.url.cors")
class CorsConfig : WebMvcConfigurer {
    var allowedOrigins: Array<String> = arrayOf()

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/api/**")
            .allowedOrigins(*allowedOrigins)
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowedHeaders(
                "Content-Type", "Authorization", "X-Requested-With", "Accept", "Origin", "Baggage"
            )
            .exposedHeaders(HttpHeaders.AUTHORIZATION)
            .allowCredentials(true)
            .maxAge(3000)
    }
}