package com.example.notiveserver.common.validation.group

import com.example.notiveserver.api.dto.archive.request.CreateBlockFormReq

class CreateBlockGroupSeqProvider :
    BaseBlockGroupProvider<CreateBlockFormReq>(CreateBlockFormReq::class.java)