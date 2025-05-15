package com.example.notiveserver.service

import com.example.notiveserver.dto.oembed.OEmbedEndpoint
import com.example.notiveserver.dto.oembed.OEmbedProvider
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.net.URI

@Component
class OEmbedProviderService(
    @Qualifier("oEmbedClient")
    private val webClient: WebClient,
) {

    private val providers: List<OEmbedProvider> by lazy { loadProviders() }

    private fun loadProviders(): List<OEmbedProvider> {
        val typeRef = object : ParameterizedTypeReference<List<OEmbedProvider>>() {}
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

    private fun OEmbedEndpoint.normalize(): OEmbedEndpoint {
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
