package com.example.notiveserver.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {

    // 1) 기본 Builder 빈 (Spring Boot가 이미 제공하므로 생략해도 됩니다)
    @Bean
    fun webClientBuilder(): WebClient.Builder = WebClient.builder()

    // 2) OEmbed 용으로 커스터마이즈된 WebClient 빈
    @Bean
    @Qualifier("oEmbedClient")
    fun oEmbedWebClient(webClientBuilder: WebClient.Builder): WebClient =
        webClientBuilder
            .defaultHeader(
                HttpHeaders.USER_AGENT,
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/112.0.0.0 Safari/537.36"
            )
            .build()
}
