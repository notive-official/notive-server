package com.example.notiveserver.api.controller

import com.example.notiveserver.api.dto.archive.ArchiveDetailRes
import com.example.notiveserver.api.dto.archive.ArchiveFormReq
import com.example.notiveserver.api.dto.archive.ArchiveRes
import com.example.notiveserver.api.dto.archive.ArchiveSummaryRes
import com.example.notiveserver.api.dto.common.ListRes
import com.example.notiveserver.api.dto.common.SliceMeta
import com.example.notiveserver.api.dto.common.SliceRes
import com.example.notiveserver.application.archive.ArchiveService
import com.example.notiveserver.application.archive.TagService
import com.example.notiveserver.application.archive.dto.BlockInfoDto
import com.example.notiveserver.common.policy.PageSize
import jakarta.validation.constraints.Min
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/archive")
class ArchiveController(
    private val archiveService: ArchiveService,
    private val tagService: TagService,
) {
    @PostMapping(
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createArchive(
        @Validated @ModelAttribute form: ArchiveFormReq
    ): ResponseEntity<ArchiveRes> {
        val blocks: List<BlockInfoDto> = form.blocks.map { block ->
            BlockInfoDto(
                position = block.position,
                type = block.type,
                payload = block.getPayload()
            )
        }
        val summary = archiveService.generateArchiveSummary(blocks)
        val archive =
            archiveService.saveArchive(
                thumbnailImage = form.thumbnailImage,
                title = form.title,
                isPublic = form.isPublic,
                type = form.type,
                isReplicable = form.isReplicable,
                summary = summary,
                groupId = UUID.fromString(form.groupId),
                tags = form.tags,
            )
        archiveService.saveArchiveBlocks(blocks, archiveId = archive.id!!)
        return ResponseEntity.ok(
            ArchiveRes(id = archive.id!!)
        )
    }

    @GetMapping("/notes")
    fun listUserArchives(
        @Min(0) @RequestParam("page") page: Int,
    ): ResponseEntity<SliceRes<ArchiveSummaryRes>> {
        val pages = archiveService.listArchivesByUser(page, PageSize.SUB)
        val sliceMeta = SliceMeta.of(pages)
        val content = pages.content.map { archive ->
            val writer = archive.writer
            val tags = tagService.listTagByArchive(archiveId = archive.id)
            ArchiveSummaryRes.of(archive = archive, tags = tags, writer = writer)
        }
        return ResponseEntity.ok(SliceRes(meta = sliceMeta, content = content))
    }

    @GetMapping("/notes/{archiveId}")
    fun getArchive(
        @PathVariable archiveId: String,
    ): ResponseEntity<ArchiveDetailRes> {
        val archive = archiveService.getArchive(archiveId = UUID.fromString(archiveId))
        val tags = tagService.listTagByArchive(archiveId = archive.meta.id)
        return ResponseEntity.ok(
            ArchiveDetailRes.of(
                meta = archive.meta,
                tags = tags,
                blocks = archive.blocks
            )
        )
    }

    @GetMapping("/tags")
    fun listTags(): ResponseEntity<ListRes<String>> {
        val tags = tagService.listTagsOwnedByUser()
        return ResponseEntity.ok(ListRes(content = tags.map { it.slug }))
    }
}