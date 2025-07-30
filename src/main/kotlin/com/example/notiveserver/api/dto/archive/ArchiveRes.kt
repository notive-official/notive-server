package com.example.notiveserver.api.dto.archive

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class ArchiveRes(
    @JsonProperty("archiveId") val archiveId: UUID
)