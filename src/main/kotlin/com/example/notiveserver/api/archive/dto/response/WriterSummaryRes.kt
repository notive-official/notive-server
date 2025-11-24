package com.example.notiveserver.api.archive.dto.response

import java.util.*

data class WriterSummaryRes(
    val id: UUID,
    val nickname: String,
    val profileImagePath: String?
)
