package com.example.notiveserver.infrastructure.oembed

import com.example.notiveserver.application.oembed.dto.OEmbedEndpointDto
import com.example.notiveserver.application.oembed.dto.OEmbedProviderDto
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.net.URI

@Component
class OEmbedProviderRegistry(
    @Qualifier("oEmbedWebClient")
    private val webClient: WebClient,
) {

    private val providers: List<OEmbedProviderDto> by lazy { loadProviders() }

    private fun loadProviders(): List<OEmbedProviderDto> {
        val typeRef = object : ParameterizedTypeReference<List<OEmbedProviderDto>>() {}
        val rawProviders = webClient.get()
            .uri("https://oembed.com/providers.json")
            .retrieve()
            .bodyToMono(typeRef)
            .block()
            .orEmpty()

        return rawProviders.map { provider ->
            provider.copy(endpoints = provider.endpoints.map { it.normalize() })
        }
    }

    private fun OEmbedEndpointDto.normalize(): OEmbedEndpointDto {
        val schemes = this.schemes
            .orEmpty()
            .flatMap { original ->
                listOf(original) + createWildcard(original)
            }
            .distinct()
        return copy(schemes = schemes)
    }

    private fun createWildcard(scheme: String): List<String> =
        runCatching {
            val cleaned = scheme.removeSuffix("/*")
            val uri = URI(cleaned)
            val parts = uri.host.split('.')
            if (parts.size < 2) {
                emptyList()
            } else {
                val base = parts.takeLast(2).joinToString(".")
                val suffix = if (scheme.endsWith("/*")) "/*" else ""
                listOf("${uri.scheme}://*.$base$suffix")
            }
        }
            .getOrDefault(emptyList())

    private fun schemeToRegex(scheme: String): Regex {
        val escaped = scheme
            .replace(".", "\\.")
            .replace("*", ".*")
        return Regex("^$escaped$")
    }

    fun findOEmbedEndpoint(targetUrl: String): String? {
        return providers
            .asSequence()
            .flatMap { it.endpoints.asSequence() }
            .firstOrNull { endpoint ->
                endpoint.schemes
                    .orEmpty()
                    .any { scheme -> schemeToRegex(scheme).matches(targetUrl) }
            }
            ?.url
    }
}
