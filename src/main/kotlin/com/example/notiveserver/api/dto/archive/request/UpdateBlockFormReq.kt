package com.example.notiveserver.api.dto.archive.request

import com.example.notiveserver.application.archive.dto.PayloadDto
import com.example.notiveserver.common.enums.BlockType
import com.example.notiveserver.common.validation.annotation.ValidImageFile
import com.example.notiveserver.common.validation.group.*
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.group.GroupSequenceProvider
import org.springframework.web.multipart.MultipartFile

@GroupSequenceProvider(UpdateBlockGroupSeqProvider::class)
data class UpdateBlockFormReq @JsonCreator constructor(
    @field:NotNull
    val id: Long,

    @field:NotNull
    @JsonProperty("position")
    val position: Int,

    @field:NotNull
    @JsonProperty("type")
    override val type: BlockType,

    @field:ValidImageFile
    @field:NotNull(groups = [ImageGroup::class])
    @JsonProperty(value = "file", access = JsonProperty.Access.WRITE_ONLY)
    private val file: MultipartFile? = null,

    @field:NotNull(groups = [TextGroup::class, LinkGroup::class])
    @JsonProperty(value = "content", access = JsonProperty.Access.WRITE_ONLY)
    private val content: String? = null
) : HasBlockType {
    fun getPayload(): PayloadDto {
        return when (type) {
            BlockType.IMAGE -> PayloadDto.File(file!!)
            BlockType.LINK -> PayloadDto.Url(content!!)
            else -> PayloadDto.Text(content!!)
        }
    }
}