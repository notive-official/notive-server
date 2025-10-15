package com.example.notiveserver.common.exception.code

import org.springframework.http.HttpStatus

internal interface ErrorCode {
    val httpStatus: HttpStatus
    val message: String
}