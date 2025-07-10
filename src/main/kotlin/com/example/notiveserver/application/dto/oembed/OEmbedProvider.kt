package com.example.notiveserver.application.dto.oembed

import com.example.notiveserver.api.dto.archive.OEmbedEndpoint
import com.fasterxml.jackson.annotation.JsonAlias

data class OEmbedProvider(
    @JsonAlias("provider_name")
    val providerName: String,

    @JsonAlias("provider_url")
    val providerUrl: String,

    val endpoints: List<OEmbedEndpoint>
)