package com.example.notiveserver.exception.code

import org.springframework.http.HttpStatus

enum class OEmbedErrorCode(override val httpStatus: HttpStatus, override val message: String) :
    ErrorCode {
    NO_ENDPOINT(HttpStatus.NOT_FOUND, "일치하는 endpoint를 찾을 수 없습니다."),
    ROBOTS_FORBIDDEN(HttpStatus.FORBIDDEN, "robots.txt에 의해 크롤링이 차단되었습니다")
}