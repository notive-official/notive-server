package com.example.notiveserver.application.oembed


import com.example.notiveserver.application.oembed.dto.OEmbedInfoDto
import com.example.notiveserver.common.exception.OEmbedException
import com.example.notiveserver.common.exception.code.OEmbedErrorCode
import com.example.notiveserver.infrastructure.oembed.RobotsChecker
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class OEmbedService(
    private val cache: OEmbedCache,
    private val robots: RobotsChecker,
    private val oEmbedClient: OEmbedHttpClient,
    private val ogParser: OpenGraphParser,
) {

    fun getOEmbed(targetUrl: String): Mono<OEmbedInfoDto> =
        cache.get(targetUrl)
            .switchIfEmpty(tryFetch(targetUrl))
            .flatMap { info -> cache.put(targetUrl, info) }

    private fun tryFetch(url: String): Mono<OEmbedInfoDto> =
        oEmbedClient.fetch(url)
            .switchIfEmpty(Mono.error(OEmbedException(OEmbedErrorCode.NO_DOMAIN_SUPPORT)))
}