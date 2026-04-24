package com.example.notiveserver.common.exception.code

import org.springframework.http.HttpStatus

enum class UserErrorCode(override val httpStatus: HttpStatus, override val message: String) :
    ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 사용자를 찾을 수 없습니다."),
    USER_PROFILE_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자의 프로필 이미지를 찾을 수 없습니다.")
}