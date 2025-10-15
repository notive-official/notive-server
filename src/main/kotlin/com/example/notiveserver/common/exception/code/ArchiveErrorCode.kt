package com.example.notiveserver.common.exception.code

import org.springframework.http.HttpStatus

enum class ArchiveErrorCode(override val httpStatus: HttpStatus, override val message: String) :
    ErrorCode {
    GROUP_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 그룹 이름입니다."),
    BOOKMARK_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 북마크입니다."),
    BOOKMARK_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 북마크입니다."),
    IMAGE_REQUIRED(HttpStatus.BAD_REQUEST, "이미지 블록에는 image가 있어야 합니다."),
    CONTENT_REQUIRED(HttpStatus.BAD_REQUEST, "이미지가 아닌 블록에는 content가 있어야 합니다.")
}