package com.example.notiveserver.api.dto.archive.request

import com.example.notiveserver.common.enums.ArchiveType
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Length
import java.util.*

data class UpdateReferenceReq(
    @field:Length(min = 1, max = 64)
    val title: String? = null,

    @field:Size(max = 20)
    val tags: List<String>? = null,

    val groupId: UUID? = null,

    val isPublic: Boolean? = null,

    val type: ArchiveType? = null,

    val isDuplicable: Boolean? = null,

    @field:Length(min = 1, max = 64)
    val url: String? = null,
) {
    @get:AssertTrue(message = "type must be REFERENCE")
    val isReferenceType: Boolean
        get() = type == ArchiveType.REFERENCE
}