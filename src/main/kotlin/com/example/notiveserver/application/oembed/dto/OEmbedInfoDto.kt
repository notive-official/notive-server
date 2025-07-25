package com.example.notiveserver.application.oembed.dto

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class OEmbedInfoDto @JsonCreator constructor(
    @JsonProperty("version")
    val version: String? = null,

    @JsonProperty("type")
    val type: String? = null,

    @JsonAlias("provider_name")
    @JsonProperty("providerName")
    val providerName: String? = null,

    @JsonAlias("provider_url")
    @JsonProperty("providerUrl")
    val providerUrl: String? = null,

    @JsonProperty("title")
    val title: String? = null,

    @JsonAlias("author_name")
    @JsonProperty("authorName")
    val authorName: String? = null,

    @JsonAlias("author_url")
    @JsonProperty("authorUrl")
    val authorUrl: String? = null,

    @JsonAlias("thumbnail_url")
    @JsonProperty("thumbnailUrl")
    val thumbnailUrl: String? = null,

    @JsonAlias("thumbnail_width")
    @JsonProperty("thumbnailWidth")
    val thumbnailWidth: Int? = null,

    @JsonAlias("thumbnail_height")
    @JsonProperty("thumbnailHeight")
    val thumbnailHeight: Int? = null,

    @JsonProperty("html")
    val html: String? = null
)