package com.example.notiveserver.api.controller

import com.example.notiveserver.api.dto.archive.ArchiveSummaryRes
import com.example.notiveserver.api.dto.common.SliceMeta
import com.example.notiveserver.api.dto.common.SliceRes
import com.example.notiveserver.application.archive.ArchiveService
import com.example.notiveserver.application.archive.TagService
import com.example.notiveserver.common.policy.PageSize
import jakarta.validation.constraints.Min
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/search")
class SearchController(
    private val archiveService: ArchiveService,
    private val tagService: TagService,
) {

    @GetMapping()
    fun getArchives(
        @Min(0) @RequestParam("page") page: Int,
    ): ResponseEntity<SliceRes<ArchiveSummaryRes>> {
        val pages = archiveService.listPublicArchives(page, PageSize.MAIN)
        val sliceMeta = SliceMeta.of(pages)
        val content = pages.content.map { archive ->
            val writer = archive.writer
            val tags = tagService.listTagByArchive(archiveId = archive.id)
            ArchiveSummaryRes.of(archive = archive, tags = tags, writer = writer)
        }
        return ResponseEntity.ok(SliceRes(meta = sliceMeta, content = content))
    }
}