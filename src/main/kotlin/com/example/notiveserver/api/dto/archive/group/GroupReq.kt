package com.example.notiveserver.api.dto.archive.group

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length

data class GroupReq(
    @field:NotNull
    @field:Length(min = 1, max = 32)
    @field:Pattern(regexp = "^[\\p{L}\\p{Nd} _-]+\$")
    val groupName: String,
)