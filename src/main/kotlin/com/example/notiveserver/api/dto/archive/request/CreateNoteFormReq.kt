package com.example.notiveserver.api.dto.archive.request

import com.example.notiveserver.application.archive.dto.BlockInfoDto
import com.example.notiveserver.common.enums.ArchiveType
import com.example.notiveserver.common.validation.annotation.ValidImageFile
import jakarta.validation.Valid
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Length
import org.springframework.web.multipart.MultipartFile
import java.util.*

data class CreateNoteFormReq(
    @field:ValidImageFile
    val thumbnailImage: MultipartFile? = null,

    @field:NotNull
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

    @field:NotNull
    @field:Size(min = 1, max = 50)
    @field:Valid
    private val _blocks: List<CreateBlockFormReq>,
) {
    @get:AssertTrue(message = "type must be NOTE")
    val isNoteType: Boolean
        get() = type == ArchiveType.NOTE

    val blocks: List<BlockInfoDto>
        get() = _blocks.map { block ->
            BlockInfoDto(
                position = block.position,
                type = block.type,
                payload = block.getPayload()
            )
        }
}

