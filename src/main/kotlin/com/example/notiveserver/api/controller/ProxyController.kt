package com.example.notiveserver.api.controller

import com.example.notiveserver.infrastructure.proxy.WebClientImageProxy
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/proxy")
class ProxyController(
    private val webClientImageProxy: WebClientImageProxy
) {
    @GetMapping("/image")
    fun proxyImage(
        @RequestParam url: String,
        @RequestParam w: Int?,
        @RequestParam q: Int?
    ): Mono<ResponseEntity<ByteArray>> {
        return webClientImageProxy.proxyImage(url, w, q)
    }
}