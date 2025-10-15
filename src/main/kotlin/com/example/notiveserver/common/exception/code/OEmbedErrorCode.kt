package com.example.notiveserver.common.exception.code

import org.springframework.http.HttpStatus

enum class OEmbedErrorCode(override val httpStatus: HttpStatus, override val message: String) :
    ErrorCode {
    NO_DOMAIN_SUPPORT(HttpStatus.NOT_FOUND, "지원하지 않는 도메인입니다."),
    OEMBED_CALL_FORBIDDEN(HttpStatus.FORBIDDEN, "oEmbed 호출 권한이 없습니다."),
    ROBOTS_FORBIDDEN(HttpStatus.FORBIDDEN, "robots.txt에 의해 크롤링이 차단되었습니다")
}