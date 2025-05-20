package com.example.notiveserver.service


import com.example.notiveserver.dto.oembed.OEmbedResponse
import com.example.notiveserver.exception.OEmbedException
import com.example.notiveserver.exception.code.OEmbedErrorCode
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.URI
import java.time.Duration

@Service
class OEmbedService(
    @Qualifier("oEmbedClient")
    private val webClient: WebClient,
    private val oEmbedProviderService: OEmbedProviderService,
    private val redisOps: ReactiveRedisOperations<String, OEmbedResponse>
) {

    companion object {
        @JvmStatic
        private val CACHE_TTL_HOURS = Duration.ofHours(1)
    }

    private fun isAllowedByRobots(targetUrl: String): Mono<Boolean> {
        val uri = URI.create(targetUrl)
        val robotsUrl = "${uri.scheme}://${uri.host}/robots.txt"
        val path = uri.rawPath

        return webClient.get()
            .uri(robotsUrl)
            .retrieve()
            .bodyToMono(String::class.java)
            .map { robotsTxt ->
                val disallows = robotsTxt.lineSequence()
                    .map { it.trim() }
                    .filter { it.startsWith("Disallow:", ignoreCase = true) }
                    .map { it.removePrefix("Disallow:").trim() }
                disallows.none { dis -> dis == "/" || path.startsWith(dis) }
            }
            .onErrorReturn(true)
    }

    private fun parseOpenGraph(targetUrl: String): Mono<OEmbedResponse> {
        return webClient.get()
            .uri(targetUrl)
            .retrieve()
            .bodyToMono(String::class.java)
            .flatMap { html ->
                val doc = Jsoup.parse(html, targetUrl)
                val title = doc.selectFirst("meta[property=og:title]")?.attr("content")
                val image = doc.selectFirst("meta[property=og:image]")?.attr("content")
                val description = doc.selectFirst("meta[property=og:description]")?.attr("content")
                val siteName = doc.selectFirst("meta[property=og:site_name]")?.attr("content")

                Mono.just(
                    OEmbedResponse(
                        version = null,
                        type = null,
                        providerName = siteName,
                        providerUrl = targetUrl,
                        title = title,
                        authorName = null,
                        authorUrl = null,
                        thumbnailUrl = image,
                        thumbnailWidth = null,
                        thumbnailHeight = null,
                        html = description // 필요하다면 description 대신 html snippet 대입
                    )
                )
            }
    }

    private fun fetchOEmbedData(oEmbedUrl: String, targetUrl: String): Mono<OEmbedResponse> {
        val uri = URI.create(oEmbedUrl)
        val scheme = uri.scheme
        val host = uri.host
        val path = uri.rawPath

        return webClient
            .get()
            .uri { b ->
                b.scheme(scheme)
                    .host(host)
                    .path(path)
                    .queryParam("url", targetUrl)
                    .queryParam("format", "json")
                    .build()
            }
            .retrieve()
            .onStatus({ it == HttpStatus.UNAUTHORIZED || it == HttpStatus.FORBIDDEN }) { resp ->
                resp.bodyToMono(String::class.java)
                    .flatMap {
                        Mono.error(OEmbedException(OEmbedErrorCode.OEMBED_CALL_FORBIDDEN))
                    }
            }
            .bodyToMono(OEmbedResponse::class.java)
    }

    fun fetchOEmbed(targetUrl: String): Mono<OEmbedResponse> {
        val cacheKey = "oembed:$targetUrl"

        return redisOps.opsForValue().get(cacheKey)
            .flatMap { cached ->
                Mono.just(cached)
            }
            .switchIfEmpty(
                Mono.defer {
                    val fetchMono = oEmbedProviderService.findOEmbedEndpoint(targetUrl)
                        ?.let { endpoint ->
                            fetchOEmbedData(endpoint, targetUrl)
                        }
                        ?: isAllowedByRobots(targetUrl)
                            .flatMap { allowed ->
                                if (!allowed) Mono.error(OEmbedException(OEmbedErrorCode.ROBOTS_FORBIDDEN))
                                else parseOpenGraph(targetUrl)
                            }

                    fetchMono.flatMap { response ->
                        redisOps
                            .opsForValue()
                            .set(cacheKey, response, CACHE_TTL_HOURS)
                            .thenReturn(response)
                    }
                }
            )
    }
}