package com.example.notiveserver.api.controller

import com.example.notiveserver.api.dto.common.ListRes
import com.example.notiveserver.api.dto.common.SliceMeta
import com.example.notiveserver.api.dto.common.SliceRes
import com.example.notiveserver.api.dto.my.archive.ArchiveFormReq
import com.example.notiveserver.api.dto.my.archive.ArchiveRes
import com.example.notiveserver.api.dto.my.archive.ArchiveSummaryRes
import com.example.notiveserver.api.dto.my.archive.WriterSummaryRes
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
            val payload = archiveService.validateAndGetPayload(block)
            BlockInfoDto(position = block.position, type = block.type, payload = payload)

        }
        val archive =
            archiveService.saveArchive(
                thumbnailImage = form.thumbnailImage,
                title = form.title,
                isPublic = form.isPublic,
                groupId = UUID.fromString(form.groupId),
                tags = form.tags,
                blocks = blocks
            )
        return ResponseEntity.ok(
            ArchiveRes(
                archiveId = archive.id!!,
            )
        )
    }

    @GetMapping("/notes")
    fun getUserArchives(
        @Min(0) @RequestParam("page") page: Int,
    ): ResponseEntity<SliceRes<ArchiveSummaryRes>> {
        val pages = archiveService.listArchivesByUser(page, PageSize.MAIN)
        val sliceMeta = SliceMeta.of(pages)
        val content = pages.content.map { archive ->
            val writer = archive.writer
            ArchiveSummaryRes(
                id = archive.id,
                thumbnailPath = archive.thumbnailPath.filePath,
                title = archive.title,
                writer = WriterSummaryRes(
                    id = writer.id,
                    nickname = writer.nickname,
                    profileImagePath = writer.profileImage.filePath
                )

            )
        }
        return ResponseEntity.ok(SliceRes(meta = sliceMeta, content = content))
    }

    @GetMapping("/tags")
    fun listTags(): ResponseEntity<ListRes<String>> {
        val tags = tagService.listTagsOwnedByUser()
        return ResponseEntity.ok(ListRes(content = tags.map { it.slug }))
    }
}