package com.example.notiveserver.application.oembed

import com.example.notiveserver.application.oembed.dto.OEmbedInfoDto
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class OpenGraphParser(@Qualifier("oEmbedWebClient") private val webClient: WebClient) {

    private fun selectFirst(doc: Document, key: String) =
        doc.selectFirst("meta[property=og:$key]")?.attr("content")

    fun parse(targetUrl: String): Mono<OEmbedInfoDto> {
        return webClient.get()
            .uri(targetUrl)
            .retrieve()
            .bodyToMono(String::class.java)
            .flatMap { html ->
                val doc = Jsoup.parse(html, targetUrl)
                val title = selectFirst(doc, "title")
                val image = selectFirst(doc, "image")
                val description = selectFirst(doc, "description")
                val siteName = selectFirst(doc, "site-name")

                Mono.just(
                    OEmbedInfoDto(
                        providerName = siteName,
                        providerUrl = targetUrl,
                        title = title,
                        thumbnailUrl = image,
                        html = description
                    )
                )
            }
    }
}