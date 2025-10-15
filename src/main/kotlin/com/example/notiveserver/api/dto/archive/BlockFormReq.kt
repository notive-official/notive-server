package com.example.notiveserver.api.dto.archive

import com.example.notiveserver.application.archive.dto.PayloadDto
import com.example.notiveserver.common.enums.BlockType
import com.example.notiveserver.common.validation.annotation.ValidImageFile
import com.example.notiveserver.common.validation.group.BlockFormReqGroupSeqProvider
import com.example.notiveserver.common.validation.group.ImageGroup
import com.example.notiveserver.common.validation.group.LinkGroup
import com.example.notiveserver.common.validation.group.TextGroup
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.group.GroupSequenceProvider
import org.springframework.web.multipart.MultipartFile

@GroupSequenceProvider(BlockFormReqGroupSeqProvider::class)
data class BlockFormReq @JsonCreator constructor(
    @field:NotNull
    @JsonProperty("position")
    val position: Int,

    @field:NotNull
    @JsonProperty("type")
    val type: BlockType,

    @field:ValidImageFile
    @field:NotNull(groups = [ImageGroup::class])
    @JsonProperty(value = "image", access = JsonProperty.Access.WRITE_ONLY)
    private val image: MultipartFile? = null,

    @field:NotNull(groups = [TextGroup::class, LinkGroup::class])
    @JsonProperty(value = "content", access = JsonProperty.Access.WRITE_ONLY)
    private val content: String? = null
) {
    fun getPayload(): PayloadDto {
        return when (type) {
            BlockType.IMAGE -> PayloadDto.File(requireNotNull(image) { "image is required" })
            BlockType.LINK -> PayloadDto.Url(requireNotNull(content) { "url is required" })
            else -> PayloadDto.Text(requireNotNull(content) { "content is required" })
        }
    }
}