package com.example.notiveserver.api.dto.archive.request

import com.example.notiveserver.application.archive.dto.BlockInfoDto
import com.example.notiveserver.application.archive.dto.BlockPositionDto
import com.example.notiveserver.common.enums.ArchiveType
import com.example.notiveserver.common.validation.annotation.ValidImageFile
import jakarta.validation.Valid
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Length
import org.springframework.web.multipart.MultipartFile
import java.util.*

data class UpdateNoteFormReq(
    @field:ValidImageFile
    val thumbnailImage: MultipartFile? = null,

    @field:Length(min = 1, max = 64)
    val title: String? = null,

    @field:Size(min = 0, max = 20)
    val tags: List<String>? = null,

    val groupId: UUID? = null,

    val isPublic: Boolean? = null,

    val type: ArchiveType? = null,

    val isDuplicable: Boolean? = null,

    @field:Size(max = 50)
    @field:Valid
    private val _addedBlocks: List<CreateBlockFormReq> = emptyList(),

    @field:Valid
    private val _updatedBlocks: List<UpdateBlockFormReq> = emptyList(),

    @field:Valid
    private val _reorderedBlocks: List<ReorderedBlockFormReq> = emptyList(),

    private val _deletedBlockIds: List<Long> = emptyList(),
) {
    @get:AssertTrue(message = "type must be NOTE")
    val isNoteType: Boolean
        get() = type == ArchiveType.NOTE

    val addedBlocks: List<BlockInfoDto>
        get() = _addedBlocks.map { block ->
            BlockInfoDto(
                position = block.position,
                type = block.type,
                payload = block.getPayload()
            )
        }

    val updatedBlocks: List<BlockInfoDto>
        get() = _updatedBlocks.map { block ->
            BlockInfoDto(
                id = block.id,
                position = block.position,
                type = block.type,
                payload = block.getPayload()
            )
        }

    val reorderedBlocks: List<BlockPositionDto>
        get() = _reorderedBlocks.map { block ->
            BlockPositionDto(
                id = block.id,
                position = block.position
            )
        }

    val deletedBlockIds: List<Long>
        get() = _deletedBlockIds
}

