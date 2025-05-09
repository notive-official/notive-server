package com.example.notiveserver.exception.code

import org.springframework.http.HttpStatus

internal interface ErrorCode {
    val httpStatus: HttpStatus
    val message: String
}