package com.example.notiveserver.api.controller

import com.example.notiveserver.api.dto.archive.OEmbedRes
import com.example.notiveserver.application.oembed.OEmbedService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/archive")
class ArchiveController(
    private val oEmbedParser: OEmbedService
) {

    @GetMapping("/oembed")
    fun oembed(@RequestParam url: String): Mono<OEmbedRes> {
        val oEmbedInfo = oEmbedParser.getOEmbed(url)
        return oEmbedInfo.map {
            OEmbedRes(
                it.type,
                it.providerName,
                it.providerUrl,
                it.title,
                it.authorName,
                it.authorUrl,
                it.thumbnailUrl,
                it.thumbnailWidth,
                it.thumbnailHeight,
                it.html
            )
        }
    }
}