package com.example.notiveserver.api.controller

import com.example.notiveserver.api.dto.my.archive.OEmbedRes
import com.example.notiveserver.application.oembed.OEmbedService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/oembed")
class OEmbedController(
    private val oEmbedParser: OEmbedService
) {
    @GetMapping()
    fun getOembed(@RequestParam url: String): Mono<OEmbedRes> {
        val oEmbedInfo = oEmbedParser.getOEmbed(url)
        return oEmbedInfo.map {
            OEmbedRes(
                type = it.type,
                providerName = it.providerName,
                providerUrl = it.providerUrl,
                title = it.title,
                authorName = it.authorName,
                authorUrl = it.authorUrl,
                thumbnailUrl = it.thumbnailUrl,
                thumbnailWidth = it.thumbnailWidth,
                thumbnailHeight = it.thumbnailHeight,
            )
        }
    }
}