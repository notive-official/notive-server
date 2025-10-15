package com.example.notiveserver.infrastructure.proxy

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class WebClientImageProxy(
    private val webClient: WebClient,
) {

    fun proxyImage(
        url: String,
        width: Int?,
        quality: Int?
    ): Mono<ResponseEntity<ByteArray>> {
        return webClient.get()
            .uri(url)
            .accept(MediaType.ALL)
            .exchangeToMono { resp ->
                if (resp.statusCode().is2xxSuccessful) {
                    resp.bodyToMono(ByteArray::class.java)
                        .map { body ->
                            val contentType = resp.headers()
                                .contentType()
                                .orElse(MediaType.APPLICATION_OCTET_STREAM)

                            ResponseEntity
                                .ok()
                                .contentType(contentType)
                                .body(body)
                        }
                } else {
                    Mono.just(ResponseEntity.status(resp.statusCode()).build())
                }
            }
    }
}