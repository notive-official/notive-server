package com.example.notiveserver.controller

import com.example.notiveserver.dto.oembed.OEmbedResponse
import com.example.notiveserver.service.OEmbedService
import com.example.notiveserver.service.ProxyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/archive")
class ArchiveController(
    private val oEmbedService: OEmbedService,
    private val proxyService: ProxyService
) {

    @GetMapping("/oembed")
    fun oembed(@RequestParam url: String): Mono<OEmbedResponse> {
        return oEmbedService.fetchOEmbed(url)
    }

    @GetMapping("/image-proxy")
    fun proxyImage(
        @RequestParam url: String,
        @RequestParam w: Int?,
        @RequestParam q: Int?
    ): Mono<ResponseEntity<ByteArray>> {
        return proxyService.proxyImage(url, w, q)
    }
}