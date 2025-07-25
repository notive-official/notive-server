package com.example.notiveserver.api.dto.archive

import com.example.notiveserver.api.validator.image.ValidImageFile
import com.example.notiveserver.common.enums.BlockType
import jakarta.validation.constraints.NotNull
import org.springframework.web.multipart.MultipartFile

data class BlockRaw(
    @field:NotNull
    val position: Int,

    @field:NotNull
    val blockType: BlockType,

    val content: String? = null,

    @field:ValidImageFile
    val image: MultipartFile? = null
)