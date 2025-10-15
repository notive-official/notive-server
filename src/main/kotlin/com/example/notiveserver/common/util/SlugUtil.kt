package com.example.notiveserver.common.util

object SlugUtil {

    private val toUnderscore = "[^a-z0-9가-힣]+".toRegex()
    private val multiUnderscore = "-{2,}".toRegex()

    fun slugify(raw: String): String =
        raw.trim()
            .lowercase()
            .replace(toUnderscore, "_")
            .replace(multiUnderscore, "_")
            .trim('_')
}