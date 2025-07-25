package com.example.notiveserver.application.common

import org.apache.commons.validator.routines.UrlValidator
import org.springframework.stereotype.Component

@Component
class UrlInspector {
    private val validator = UrlValidator(arrayOf("http", "https"))

    fun isSafe(url: String): Boolean =
        validator.isValid(url) && !url.contains("example-phishing.com")
}