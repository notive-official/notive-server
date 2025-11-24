package com.example.notiveserver.api.archive.dto.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

data class ReorderedBlockFormReq @JsonCreator constructor(
    @field:NotNull
    @JsonProperty("id")
    val id: Long,

    @field:NotNull
    @JsonProperty("position")
    val position: Int,
)