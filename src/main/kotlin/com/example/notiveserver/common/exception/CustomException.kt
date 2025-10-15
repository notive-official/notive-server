package com.example.notiveserver.common.exception

import com.example.notiveserver.common.exception.code.ErrorCode

sealed class CustomException(
    errorCode: ErrorCode,
) : RuntimeException(errorCode.message) {
    val httpStatus = errorCode.httpStatus
}
