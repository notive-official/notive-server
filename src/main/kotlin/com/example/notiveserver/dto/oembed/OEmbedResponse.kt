package com.example.notiveserver.dto.oembed

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class OEmbedResponse(
    val version: String?,
    val type: String?,
    @JsonAlias("provider_name")
    val providerName: String?,

    @JsonAlias("provider_url")
    val providerUrl: String?,

    val title: String?,

    @JsonAlias("author_name")
    val authorName: String?,

    @JsonAlias("author_url")
    val authorUrl: String?,

    @JsonAlias("thumbnail_url")
    val thumbnailUrl: String?,

    @JsonAlias("thumbnail_width")
    val thumbnailWidth: Int?,

    @JsonAlias("thumbnail_height")
    val thumbnailHeight: Int?,
    val html: String?
)