package com.example.notiveserver.common.validation.group

import com.example.notiveserver.api.archive.dto.request.UpdateBlockFormReq


class UpdateBlockGroupSeqProvider :
    BaseBlockGroupProvider<UpdateBlockFormReq>(UpdateBlockFormReq::class.java)