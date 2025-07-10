package com.example.notiveserver.application.oembed

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.URI

@Component
class RobotsChecker(private val webClient: WebClient) {
    fun isAllowed(url: String): Mono<Boolean> {
        val uri = URI.create(url)
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
}