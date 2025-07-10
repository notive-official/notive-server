package com.example.notiveserver.common.exception

import com.example.notiveserver.common.exception.code.AuthErrorCode

class AuthException(errorCode: AuthErrorCode) : CustomException(errorCode)