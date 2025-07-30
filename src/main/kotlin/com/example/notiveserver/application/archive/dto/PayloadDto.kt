package com.example.notiveserver.application.archive.dto

import org.springframework.web.multipart.MultipartFile

sealed class PayloadDto {
    data class File(val file: MultipartFile) : PayloadDto()
    data class Text(val text: String) : PayloadDto()
}
