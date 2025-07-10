package com.example.notiveserver.application.oembed

import com.example.notiveserver.application.dto.oembed.OEmbedInfo
import com.example.notiveserver.common.exception.OEmbedException
import com.example.notiveserver.common.exception.code.OEmbedErrorCode
import com.example.notiveserver.infrastructure.oembed.OEmbedProviderRegistry
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Mono
import java.net.URI

@Component
class OEmbedHttpClient(
    @Qualifier("oEmbedWebClient")
    private val webClient: WebClient,
    private val providerRegistry: OEmbedProviderRegistry
) {

    fun fetch(targetUrl: String): Mono<OEmbedInfo> =
        Mono.justOrEmpty(providerRegistry.findOEmbedEndpoint(targetUrl))
            .flatMap { endpoint ->
                webClient.get()
                    .uri(buildOEmbedUri(endpoint, targetUrl))
                    .retrieve()
                    .onStatus(::isForbidden, ::oEmbedForbidden)
                    .bodyToMono(OEmbedInfo::class.java)
            }

    private fun buildOEmbedUri(oEmbedUrl: String, targetUrl: String): (UriBuilder) -> URI = { b ->
        val base = URI.create(oEmbedUrl)
        b.scheme(base.scheme)
            .host(base.host)
            .path(base.rawPath)
            .queryParam("url", targetUrl)
            .queryParam("format", "json")
            .build()
    }

    private fun isForbidden(statusCode: HttpStatusCode): Boolean =
        statusCode == HttpStatus.UNAUTHORIZED || statusCode == HttpStatus.FORBIDDEN

    private fun oEmbedForbidden(resp: ClientResponse): Mono<Throwable> =
        resp.bodyToMono(String::class.java)
            .flatMap { Mono.error(OEmbedException(OEmbedErrorCode.OEMBED_CALL_FORBIDDEN)) }
}