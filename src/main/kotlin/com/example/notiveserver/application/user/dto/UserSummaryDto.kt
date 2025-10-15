package com.example.notiveserver.application.user.dto

import java.util.*

data class UserSummaryDto(
    val id: UUID,
    val nickname: String,
    val profileImage: ProfileImageDto,
)