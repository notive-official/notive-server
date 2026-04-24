package com.example.notiveserver.api.archive.dto.request

data class ArchiveSearchConditionReq(
    private val tags: String?,
    val q: String?,
) {
    val tagList: List<String>?
        get() = tags
            ?.split(",")
            ?.filter { it.isNotBlank() }
}