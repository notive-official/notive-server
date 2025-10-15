package com.example.notiveserver.common.exception

import com.example.notiveserver.common.exception.code.UserErrorCode

class UserException(errorCode: UserErrorCode) : CustomException(errorCode)