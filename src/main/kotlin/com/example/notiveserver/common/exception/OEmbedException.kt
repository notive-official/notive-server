package com.example.notiveserver.common.exception

import com.example.notiveserver.common.exception.code.OEmbedErrorCode

class OEmbedException(errorCode: OEmbedErrorCode) : CustomException(errorCode)