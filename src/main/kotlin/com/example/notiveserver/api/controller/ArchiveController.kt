package com.example.notiveserver.api.controller

import com.example.notiveserver.api.dto.archive.*
import com.example.notiveserver.api.dto.common.ListRes
import com.example.notiveserver.api.dto.common.SliceMeta
import com.example.notiveserver.api.dto.common.SliceRes
import com.example.notiveserver.application.archive.ArchiveService
import com.example.notiveserver.application.archive.BookmarkService
import com.example.notiveserver.application.archive.TagService
import com.example.notiveserver.application.archive.dto.BlockInfoDto
import com.example.notiveserver.application.archive.dto.PayloadDto
import com.example.notiveserver.application.oembed.OEmbedService
import com.example.notiveserver.common.enums.ArchiveType
import com.example.notiveserver.common.enums.BlockType
import com.example.notiveserver.common.enums.ImageCategory
import com.example.notiveserver.common.policy.PageSize
import com.example.notiveserver.infrastructure.s3.S3StorageClient
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
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
    private val bookmarkService: BookmarkService,
    private val oEmbedService: OEmbedService,
    private val s3StorageClient: S3StorageClient,
) {
    @PostMapping(
        "/note",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createNote(
        @Validated @ModelAttribute form: NoteFormReq
    ): ResponseEntity<ArchiveRes> {
        val blocks: List<BlockInfoDto> = form.blocks.map { block ->
            BlockInfoDto(
                position = block.position,
                type = block.type,
                payload = block.getPayload()
            )
        }
        val summary = archiveService.generateArchiveSummary(blocks)
        val thumbnailPath = form.thumbnailImage?.let { file ->
            s3StorageClient.saveImage(file, ImageCategory.ARCHIVE_THUMBNAIL)
        }
        val archive =
            archiveService.saveArchive(
                thumbnailPath = thumbnailPath,
                title = form.title,
                isPublic = form.isPublic,
                type = form.type,
                isDuplicable = form.isDuplicable,
                summary = summary,
                groupId = form.groupId,
                tags = form.tags,
            )
        archiveService.saveArchiveBlocks(blocks, archiveId = archive.id!!)
        return ResponseEntity.ok(
            ArchiveRes(id = archive.id!!)
        )
    }

    @PostMapping("/reference")
    fun createReference(
        @Validated @RequestBody body: ReferenceReq
    ): ResponseEntity<ArchiveRes> {
        val oEmbedInfo = oEmbedService.getOEmbed(body.url).block()
        val archive = archiveService.saveArchive(
            thumbnailPath = oEmbedInfo?.thumbnailUrl,
            title = body.title,
            isPublic = body.isPublic,
            type = body.type,
            isDuplicable = body.isDuplicable,
            summary = "",
            groupId = body.groupId,
            tags = body.tags,
        )

        val linkBlock = BlockInfoDto(
            position = 0,
            type = BlockType.LINK,
            payload = PayloadDto.Url(url = body.url)
        )
        archiveService.saveArchiveBlocks(listOf(linkBlock), archiveId = archive.id!!)
        return ResponseEntity.ok(
            ArchiveRes(id = archive.id!!)
        )
    }

    @GetMapping("")
    fun listUserArchives(
        @Min(0) @RequestParam("page") page: Int,
        @RequestParam("type") archiveType: ArchiveType?,
    ): ResponseEntity<SliceRes<ArchiveSummaryRes>> {
        val pages = archiveService.listArchivesByUser(page, PageSize.SUB, archiveType)
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
        @PathVariable archiveId: UUID,
    ): ResponseEntity<ArchiveDetailRes> {
        val archive = archiveService.getArchive(archiveId = archiveId)
        val tags = tagService.listTagByArchive(archiveId = archiveId)
        val canEdit = archiveService.canEditArchive(archiveId = archiveId)
        val isMarked = bookmarkService.isMarked(archiveId = archiveId)
        return ResponseEntity.ok(
            ArchiveDetailRes.of(
                meta = archive.meta,
                canEdit = canEdit,
                isMarked = isMarked,
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

    @PostMapping("/{archiveId}/bookmark")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun createBookmark(
        @PathVariable archiveId: UUID,
    ) {
        bookmarkService.markArchive(archiveId = archiveId)
    }

    @DeleteMapping("/{archiveId}/bookmark")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteBookmark(
        @PathVariable archiveId: UUID,
    ) {
        bookmarkService.unmarkArchive(archiveId = archiveId)
    }
}