package com.example.notiveserver.common.exception

import com.example.notiveserver.common.exception.code.ArchiveErrorCode

class ArchiveException(errorCode: ArchiveErrorCode) : CustomException(errorCode)