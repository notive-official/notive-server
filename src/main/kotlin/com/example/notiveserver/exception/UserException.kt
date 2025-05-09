package com.example.notiveserver.exception

import com.example.notiveserver.exception.code.UserErrorCode

class UserException(errorCode: UserErrorCode) : CustomException(errorCode)