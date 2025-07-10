package com.example.notiveserver.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.time.Duration

@Configuration
class WebClientConfig {

    // 1) 기본 Builder 빈 (Spring Boot가 이미 제공하므로 생략해도 됩니다)
    @Bean
    fun webClientBuilder(): WebClient.Builder = WebClient.builder()

    @Bean
    fun webClient(webClientBuilder: WebClient.Builder): WebClient {
        return webClientBuilder
            .defaultHeader(
                HttpHeaders.USER_AGENT,
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/112.0.0.0 Safari/537.36"
            )
            .build()
    }

    @Bean
    @Qualifier("oEmbedWebClient")
    fun oEmbedWebClient(webClientBuilder: WebClient.Builder): WebClient {
        val provider = ConnectionProvider.builder("oembedPool")   // ① 풀 이름
            .maxConnections(100)                  // ② 커넥션 상한
            .pendingAcquireMaxCount(200)          // ③ 대기 슬롯
            .maxIdleTime(Duration.ofSeconds(30))  // ④ idle 커넥션 정리
            .metrics(true)                        // ⑤ Micrometer 연동
            .build()

        val httpClient = HttpClient.create(provider)
            .responseTimeout(Duration.ofSeconds(3))
        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .defaultHeader(
                HttpHeaders.USER_AGENT,
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/112.0.0.0 Safari/537.36"
            )
            .build()
    }
}
