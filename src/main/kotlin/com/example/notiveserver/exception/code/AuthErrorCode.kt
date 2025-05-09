package com.example.notiveserver.exception.code

import org.springframework.http.HttpStatus

enum class AuthErrorCode(override val httpStatus: HttpStatus, override val message: String) :
    ErrorCode {
    CANNOT_REISSUE(HttpStatus.UNAUTHORIZED, "인증 정보를 재발급 받을 수 없습니다.")
}