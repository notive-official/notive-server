package com.example.notiveserver.api.dto.archive

import com.example.notiveserver.common.enums.ArchiveType
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Length
import java.util.*

data class ReferenceReq(
    @field:NotBlank
    @field:Length(min = 1, max = 64)
    val title: String,

    @field:Size(min = 0, max = 20)
    val tags: List<String> = emptyList(),

    @field:NotNull
    val groupId: UUID,

    @field:NotNull
    val isPublic: Boolean,

    @field:NotNull
    val type: ArchiveType,

    @field:NotNull
    val isDuplicable: Boolean,

    @field:NotBlank
    val url: String,
) {
    @get:AssertTrue(message = "type must be REFERENCE")
    val isReferenceType: Boolean
        get() = type == ArchiveType.REFERENCE
}