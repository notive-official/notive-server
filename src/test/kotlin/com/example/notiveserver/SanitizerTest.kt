package com.example.notiveserver

import com.example.notiveserver.application.common.HtmlSanitizer
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SanitizerTest @Autowired constructor(
    private val sanitizer: HtmlSanitizer
) {

    @Test
    fun `javascript 프로토콜이 제거된다`() {
        val dirty = """<a href="javascript:alert(1)">click</a>"""
        sanitizer.clean(dirty) shouldBe "<a>click</a>"
    }
}