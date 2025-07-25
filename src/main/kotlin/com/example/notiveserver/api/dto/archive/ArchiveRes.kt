package com.example.notiveserver.api.dto.archive

import com.fasterxml.jackson.annotation.JsonProperty

data class ArchiveRes(
    @JsonProperty("title") val title: String
)