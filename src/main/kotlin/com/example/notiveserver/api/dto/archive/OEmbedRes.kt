package com.example.notiveserver.api.dto.archive

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class OEmbedRes(
    val type: String? = null,
    val providerName: String? = null,
    val providerUrl: String? = null,
    val title: String? = null,
    val authorName: String? = null,
    val authorUrl: String? = null,
    val thumbnailUrl: String? = null,
    val thumbnailWidth: Int? = null,
    val thumbnailHeight: Int? = null,
    val html: String? = null
)