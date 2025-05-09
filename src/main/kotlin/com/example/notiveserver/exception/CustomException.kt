package com.example.notiveserver.exception

import com.example.notiveserver.exception.code.ErrorCode

sealed class CustomException(
    errorCode: ErrorCode,
) : RuntimeException(errorCode.message) {
    val httpStatus = errorCode.httpStatus
}
