package com.example.notiveserver.application.common

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Safelist
import org.springframework.stereotype.Component

@Component
class HtmlSanitizer {

    private val safelist: Safelist = Safelist.relaxed()
        .addTags("iframe")
        .addAttributes("iframe", "src", "width", "height", "allow", "allowfullscreen")
        .addProtocols("iframe", "src", "http", "https")
    private val out = Document.OutputSettings().prettyPrint(false)

    fun clean(raw: String): String = Jsoup.clean(raw, "", safelist, out)
}