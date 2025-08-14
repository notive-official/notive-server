package com.example.notiveserver.api.dto.archive

import java.util.*

data class WriterSummaryRes(
    val id: UUID,
    val nickname: String,
    val profileImagePath: String
)
