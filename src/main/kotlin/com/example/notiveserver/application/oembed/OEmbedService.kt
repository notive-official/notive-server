package com.example.notiveserver.application.oembed


import com.example.notiveserver.application.dto.oembed.OEmbedInfo
import com.example.notiveserver.common.exception.OEmbedException
import com.example.notiveserver.common.exception.code.OEmbedErrorCode
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class OEmbedService(
    private val cache: OEmbedCache,
    private val robots: RobotsChecker,
    private val oEmbedClient: OEmbedHttpClient,
    private val ogParser: OpenGraphParser,
) {

    fun getOEmbed(targetUrl: String): Mono<OEmbedInfo> =
        cache.get(targetUrl)
            .switchIfEmpty(tryFetch(targetUrl))
            .flatMap { info -> cache.put(targetUrl, info) }

    private fun tryFetch(url: String): Mono<OEmbedInfo> =
        oEmbedClient.fetch(url)
            .switchIfEmpty(
                robots.isAllowed(url)
                    .flatMap { allowed ->
                        if (!allowed) Mono.error(OEmbedException(OEmbedErrorCode.ROBOTS_FORBIDDEN))
                        else ogParser.parse(url)
                    }
            )
}