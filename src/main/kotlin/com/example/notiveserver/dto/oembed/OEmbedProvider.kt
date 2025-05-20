package com.example.notiveserver.dto.oembed

import com.fasterxml.jackson.annotation.JsonAlias

data class OEmbedProvider(
    @JsonAlias("provider_name")
    val providerName: String,

    @JsonAlias("provider_url")
    val providerUrl: String,

    val endpoints: List<OEmbedEndpoint>
)