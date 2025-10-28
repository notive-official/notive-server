package com.example.notiveserver.common.exception.code

import org.springframework.http.HttpStatus

enum class ArchiveErrorCode(override val httpStatus: HttpStatus, override val message: String) :
    ErrorCode {
    GROUP_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 그룹입니다."),
    GROUP_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 그룹 이름입니다."),
    ARCHIVE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 아카이브입니다."),
    ARCHIVE_BLOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 블록입니다."),
    NOT_ARCHIVE_OWNER(HttpStatus.FORBIDDEN, "아카이브 소유자 권한이 필요한 요청입니다."),
    NOT_GROUP_OWNER(HttpStatus.FORBIDDEN, "그룹 소유자 권한이 필요한 요청입니다."),
    BOOKMARK_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 북마크입니다."),
    BOOKMARK_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 북마크입니다."),
    IMAGE_REQUIRED(HttpStatus.BAD_REQUEST, "이미지 블록에는 image가 있어야 합니다."),
    CONTENT_REQUIRED(HttpStatus.BAD_REQUEST, "이미지가 아닌 블록에는 content가 있어야 합니다.")
}