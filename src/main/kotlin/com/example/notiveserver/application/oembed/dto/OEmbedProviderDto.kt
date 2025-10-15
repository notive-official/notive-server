package com.example.notiveserver.application.oembed.dto

import com.fasterxml.jackson.annotation.JsonAlias

data class OEmbedProviderDto(
    @JsonAlias("provider_name")
    val providerName: String,

    @JsonAlias("provider_url")
    val providerUrl: String,

    val endpoints: List<OEmbedEndpointDto>
)