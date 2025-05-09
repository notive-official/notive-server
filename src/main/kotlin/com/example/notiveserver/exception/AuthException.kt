package com.example.notiveserver.exception

import com.example.notiveserver.exception.code.AuthErrorCode

class AuthException(errorCode: AuthErrorCode) : CustomException(errorCode)