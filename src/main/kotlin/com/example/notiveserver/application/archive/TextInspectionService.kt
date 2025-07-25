package com.example.notiveserver.application.archive

import com.example.notiveserver.application.common.HtmlSanitizer
import com.example.notiveserver.application.common.UrlInspector
import org.springframework.stereotype.Service

@Service
class TextInspectionService(
    private val sanitizer: HtmlSanitizer,
    private val urlInspector: UrlInspector,
) {
//    fun inspectAndNormalize(blocks: List<BlockContentDto>): List<BlockContentDto> =
//        blocks.mapIndexed { idx, b ->
//            val text = (b.data["text"] as? String).orEmpty()
//
//            // 1) 길이 제한
//            require(text.length <= 10_000) { "블록 $idx: 글자 수 초과(10k)" }
//
//            // 2) XSS / HTML 정제
//            val cleaned = sanitizer.clean(text)
//
//            // 3) URL 검사 (markdown, 링크 태그 등에만 선택 적용)
//            Regex("""https?://\S+""").findAll(cleaned).forEach {
//                require(urlInspector.isSafe(it.value)) { "블록 $idx: 허용되지 않은 URL" }
//            }
//
//            // 결과 반영
//            b.copy(data = b.data + ("text" to finalText))
//        }
}