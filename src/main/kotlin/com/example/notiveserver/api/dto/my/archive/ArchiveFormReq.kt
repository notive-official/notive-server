package com.example.notiveserver.api.dto.my.archive

import com.example.notiveserver.api.validator.image.ValidImageFile
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Length
import org.springframework.web.multipart.MultipartFile

data class ArchiveFormReq(
    @field:ValidImageFile
    val thumbnailImage: MultipartFile? = null,

    @field:NotNull
    @field:Length(min = 1, max = 64)
    val title: String,

    @field:Size(min = 0, max = 20)
    val tags: List<String> = emptyList(),

    @field:NotBlank
    val groupId: String,

    @field:NotNull
    val isPublic: Boolean,

    @field:NotNull
    @field:Size(min = 1, max = 50)
    @field:Valid
    val blocks: List<BlockFormReq>,
)

