package com.example.notiveserver.exception

import com.example.notiveserver.exception.code.OEmbedErrorCode

class OEmbedException(errorCode: OEmbedErrorCode) : CustomException(errorCode)