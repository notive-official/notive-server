package com.example.notiveserver.dto.oembed

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class OEmbedResponse @JsonCreator constructor(
    @JsonProperty("version")
    val version: String?,

    @JsonProperty("type")
    val type: String?,

    @JsonAlias("provider_name")
    @JsonProperty("providerName")
    val providerName: String?,

    @JsonAlias("provider_url")
    @JsonProperty("providerUrl")
    val providerUrl: String?,

    @JsonProperty("title")
    val title: String?,

    @JsonAlias("author_name")
    @JsonProperty("authorName")
    val authorName: String?,

    @JsonAlias("author_url")
    @JsonProperty("authorUrl")
    val authorUrl: String?,

    @JsonAlias("thumbnail_url")
    @JsonProperty("thumbnailUrl")
    val thumbnailUrl: String?,

    @JsonAlias("thumbnail_width")
    @JsonProperty("thumbnailWidth")
    val thumbnailWidth: Int?,

    @JsonAlias("thumbnail_height")
    @JsonProperty("thumbnailHeight")
    val thumbnailHeight: Int?,

    @JsonProperty("html")
    val html: String?
)