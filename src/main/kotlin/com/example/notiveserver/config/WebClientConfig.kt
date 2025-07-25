package com.example.notiveserver.config

import io.netty.handler.ssl.SslContextBuilder
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.time.Duration

@Configuration
class WebClientConfig {

    // 1) 기본 Builder 빈 (Spring Boot가 이미 제공하므로 생략해도 됩니다)
    @Bean
    fun webClientBuilder(): WebClient.Builder = WebClient.builder()

    @Bean
    @Qualifier("oEmbedWebClient")
    fun oEmbedWebClient(webClientBuilder: WebClient.Builder): WebClient {

        val provider = ConnectionProvider.builder("oembedPool")   // ① 풀 이름
            .maxConnections(100)                  // ② 커넥션 상한
            .pendingAcquireMaxCount(200)          // ③ 대기 슬롯
            .maxIdleTime(Duration.ofSeconds(30))  // ④ idle 커넥션 정리
            .metrics(true)                        // ⑤ Micrometer 연동
            .build()

        val strategies = ExchangeStrategies.builder()
            .codecs { cfg ->
                cfg.defaultCodecs().maxInMemorySize(1 * 1024 * 1024)  // 1MB
            }
            .build()

        val httpClient = HttpClient.create(provider)
            .secure { spec ->
                spec.sslContext(SslContextBuilder.forClient().build())  // TLS 1.2+ 강제
            }
            .responseTimeout(Duration.ofSeconds(3))
            .followRedirect(false)

        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .exchangeStrategies(strategies)
            .defaultHeader(
                HttpHeaders.USER_AGENT,
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/112.0.0.0 Safari/537.36"
            )
            // ④ HTTPS 전용 스킴 검증 + Referer 헤더 제거
            .filter { request, next ->
                val uri = request.url()
                if (uri.scheme != "https") {
                    Mono.error(IllegalArgumentException("HTTPS 전용 URL만 허용됩니다."))
                } else {
                    // Referer 헤더 제거
                    val filtered = ClientRequest.from(request)
                        .headers { headers -> headers.remove(HttpHeaders.REFERER) }
                        .build()
                    next.exchange(filtered)
                }
            }
            .build()
    }
}
