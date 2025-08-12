package com.example.notiveserver.api.dto.my.archive

import com.example.notiveserver.api.validator.image.ValidImageFile
import com.example.notiveserver.common.enums.BlockType
import jakarta.validation.constraints.NotNull
import org.springframework.web.multipart.MultipartFile

data class BlockFormReq(
    @field:NotNull
    val position: Int,

    @field:NotNull
    val type: BlockType,

    @field:ValidImageFile
    val image: MultipartFile? = null,

    val content: String? = null
)