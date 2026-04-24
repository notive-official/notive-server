package com.example.notiveserver.api.archive

import com.example.notiveserver.api.archive.dto.request.*
import com.example.notiveserver.api.archive.dto.response.ArchiveDetailRes
import com.example.notiveserver.api.archive.dto.response.ArchiveRes
import com.example.notiveserver.api.archive.dto.response.ArchiveSummaryRes
import com.example.notiveserver.api.dto.ListRes
import com.example.notiveserver.api.dto.SliceMeta
import com.example.notiveserver.api.dto.SliceRes
import com.example.notiveserver.application.archive.ArchiveBlockService
import com.example.notiveserver.application.archive.ArchiveSearchService
import com.example.notiveserver.application.archive.ArchiveService
import com.example.notiveserver.application.archive.TagService
import com.example.notiveserver.application.archive.dto.ArchiveSearchCondition
import com.example.notiveserver.application.archive.dto.BlockInfoDto
import com.example.notiveserver.application.archive.dto.PayloadDto
import com.example.notiveserver.application.bookmark.BookmarkService
import com.example.notiveserver.application.oembed.OEmbedService
import com.example.notiveserver.common.enums.ArchiveType
import com.example.notiveserver.common.enums.BlockType
import com.example.notiveserver.common.policy.PageSize
import jakarta.validation.constraints.Min
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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
    private val archiveBlockService: ArchiveBlockService,
    private val archiveSearchService: ArchiveSearchService
) {
    @PostMapping(
        "/note",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createNote(
        @Validated @ModelAttribute form: CreateNoteFormReq
    ): ResponseEntity<ArchiveRes> {
        val blocks = form.blocks
        val summary = archiveService.generateArchiveSummary(blocks)
        val archive =
            archiveService.saveArchive(
                thumbnailImage = form.thumbnailImage,
                title = form.title,
                isPublic = form.isPublic,
                type = form.type,
                isDuplicable = form.isDuplicable,
                summary = summary,
                groupId = form.groupId,
            )
        val tags = tagService.saveTagsByArchive(form.tags, archive.id!!)
        archiveBlockService.saveArchiveBlocks(archiveId = archive.id!!, blocks)
        return ResponseEntity.ok(
            ArchiveRes(id = archive.id!!)
        )
    }


    @PatchMapping(
        "/note/{archiveId}",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun updateNote(
        @Validated @ModelAttribute form: UpdateNoteFormReq,
        @PathVariable archiveId: UUID,
    ): ResponseEntity<ArchiveRes> {
        println(form.toString())
        val addedBlocks = form.addedBlocks
        val summary = archiveService.generateArchiveSummary(addedBlocks)
        if (form.isThumbnailDeleted) archiveService.deleteThumbnail(archiveId)
        val archive =
            archiveService.updateArchive(
                archiveId = archiveId,
                thumbnailImage = form.thumbnailImage,
                title = form.title,
                isPublic = form.isPublic,
                type = form.type,
                isDuplicable = form.isDuplicable,
                summary = summary,
                groupId = form.groupId,
            )
        form.tags?.let { tagService.updateTagsByArchive(form.tags, archiveId) }
        archiveBlockService.deleteArchiveBlocks(
            archiveId = archiveId,
            blockIds = form.deletedBlockIds
        )
        archiveBlockService.reorderArchiveBlocks(
            archiveId = archiveId,
            blocks = form.reorderedBlocks
        )
        archiveBlockService.updateArchiveBlocksPayload(
            archiveId = archiveId,
            blocks = form.updatedBlocks
        )
        archiveBlockService.saveArchiveBlocks(archiveId = archiveId, blocks = addedBlocks)
        return ResponseEntity.ok(
            ArchiveRes(id = archiveId)
        )
    }

    @PostMapping("/reference")
    fun createReference(
        @Validated @RequestBody body: CreateReferenceReq
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
        )
        val tags = tagService.saveTagsByArchive(body.tags, archive.id!!)
        val linkBlock = BlockInfoDto(
            position = 0,
            type = BlockType.LINK,
            payload = PayloadDto.Url(url = body.url)
        )
        archiveBlockService.saveArchiveBlocks(archiveId = archive.id!!, listOf(linkBlock))
        return ResponseEntity.ok(
            ArchiveRes(id = archive.id!!)
        )
    }

    @PutMapping("/reference/{archiveId}")
    fun updateReference(
        @Validated @RequestBody body: UpdateReferenceReq,
        @PathVariable archiveId: UUID
    ): ResponseEntity<ArchiveRes> {
        val oEmbedInfo = body.url?.let { oEmbedService.getOEmbed(body.url).block() }
        val archive = archiveService.updateArchive(
            archiveId = archiveId,
            thumbnailPath = oEmbedInfo?.thumbnailUrl,
            title = body.title,
            isPublic = body.isPublic,
            type = body.type,
            isDuplicable = body.isDuplicable,
            summary = "",
            groupId = body.groupId,
        )
        body.tags?.let { tagService.updateTagsByArchive(body.tags, archiveId) }
        body.url?.let {
            val linkBlock = BlockInfoDto(
                position = 0,
                type = BlockType.LINK,
                payload = PayloadDto.Url(url = body.url)
            )
            archiveBlockService.updateArchiveBlocksPayload(
                archiveId = archive.id!!,
                listOf(linkBlock)
            )
        }
        return ResponseEntity.ok(
            ArchiveRes(id = archive.id!!)
        )
    }

    @GetMapping("")
    fun listArchivesByUser(
        @Min(0) @RequestParam("page") page: Int,
        @RequestParam("type") archiveType: ArchiveType?,
    ): ResponseEntity<SliceRes<ArchiveSummaryRes>> {
        val pages = archiveSearchService.listArchivesByUser(
            pageable = PageRequest.of(
                page, PageSize.SUB,
                Sort.by(Sort.Direction.DESC, "createdAt")
            ),
            archiveType = archiveType
        )
        val sliceMeta = SliceMeta.of(pages)
        val content = pages.content.map { archive ->
            val writer = archive.writer
            val tags = tagService.listTagsByArchive(archiveId = archive.id)
            ArchiveSummaryRes.of(archive = archive, tags = tags, writer = writer)
        }
        return ResponseEntity.ok(SliceRes(meta = sliceMeta, content = content))
    }

    @GetMapping("/bookmarks")
    fun listBookmarkedArchivesByUser(
        @Min(0) @RequestParam("page") page: Int,
    ): ResponseEntity<SliceRes<ArchiveSummaryRes>> {
        val pages = bookmarkService.listBookmarkedArchivesByUser(
            pageable = PageRequest.of(
                page, PageSize.SUB,
                Sort.by(Sort.Direction.DESC, "updatedAt")
            )
        )
        val sliceMeta = SliceMeta.of(pages)
        val content = pages.content.map { bookmark ->
            val writer = bookmark.archive.writer
            val tags = tagService.listTagsByArchive(archiveId = bookmark.archive.id)
            ArchiveSummaryRes.of(bookmark.archive, tags, writer)
        }
        return ResponseEntity.ok(SliceRes(meta = sliceMeta, content = content))
    }

    @GetMapping("/{archiveId}")
    fun getDetailedArchive(
        @PathVariable archiveId: UUID,
    ): ResponseEntity<ArchiveDetailRes> {
        val archive = archiveService.getArchive(archiveId = archiveId)
        val tags = tagService.listTagsByArchive(archiveId = archiveId)
        val isOwner = archiveService.isArchiveOwner(archiveId = archiveId)
        val isMarked = bookmarkService.isMarked(archiveId = archiveId)
        return ResponseEntity.ok(
            ArchiveDetailRes.of(
                meta = archive.meta,
                canEdit = isOwner,
                canDelete = isOwner,
                canDuplicate = archive.meta.isDuplicable,
                isMarked = isMarked,
                tags = tags,
                blocks = archive.blocks

            )
        )
    }

    @GetMapping("/search")
    fun searchArchives(
        @Min(0) @RequestParam("page") page: Int,
        condition: ArchiveSearchConditionReq
    ): ResponseEntity<SliceRes<ArchiveSummaryRes>> {
        val pages = archiveSearchService.searchArchives(
            cond = ArchiveSearchCondition(tags = condition.tagList, q = condition.q),
            pageable = PageRequest.of(
                page,
                PageSize.MAIN,
                Sort.by(Sort.Direction.DESC, "createdAt")
            )
        )
        val sliceMeta = SliceMeta.of(pages)
        val content = pages.content.map { archive ->
            val writer = archive.writer
            val tags = tagService.listTagsByArchive(archiveId = archive.id)
            ArchiveSummaryRes.of(archive = archive, tags = tags, writer = writer)
        }
        return ResponseEntity.ok(SliceRes(meta = sliceMeta, content = content))
    }

    @DeleteMapping("/{archiveId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteArchive(
        @PathVariable archiveId: UUID,
    ) {
        archiveService.deleteArchive(archiveId = archiveId)
    }

    @GetMapping("/tags")
    fun listTags(): ResponseEntity<ListRes<String>> {
        val tags = tagService.listTagsOwnedByUser()
        return ResponseEntity.ok(ListRes(content = tags))
    }
}