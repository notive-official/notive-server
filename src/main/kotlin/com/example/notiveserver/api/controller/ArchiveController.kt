package com.example.notiveserver.api.controller

import com.example.notiveserver.api.dto.archive.ArchiveFormReq
import com.example.notiveserver.api.dto.archive.ArchiveRes
import com.example.notiveserver.api.dto.archive.OEmbedRes
import com.example.notiveserver.api.dto.archive.group.GroupDetailRes
import com.example.notiveserver.api.dto.archive.group.GroupReq
import com.example.notiveserver.api.dto.archive.group.GroupSummaryRes
import com.example.notiveserver.api.dto.common.ListRes
import com.example.notiveserver.api.dto.common.SliceMeta
import com.example.notiveserver.api.dto.common.SliceRes
import com.example.notiveserver.application.archive.ArchiveService
import com.example.notiveserver.application.archive.GroupService
import com.example.notiveserver.application.archive.TagService
import com.example.notiveserver.application.archive.dto.BlockInfoDto
import com.example.notiveserver.application.archive.dto.GroupSummaryDto
import com.example.notiveserver.application.oembed.OEmbedService
import com.example.notiveserver.common.policy.PageSize
import com.example.notiveserver.infrastructure.security.dto.CustomUser
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/api/archive")
class ArchiveController(
    private val oEmbedParser: OEmbedService,
    private val archiveService: ArchiveService,
    private val groupService: GroupService,
    private val tagService: TagService,
) {

    @GetMapping("/oembed")
    fun getOembed(@RequestParam url: String): Mono<OEmbedRes> {
        val oEmbedInfo = oEmbedParser.getOEmbed(url)
        return oEmbedInfo.map {
            OEmbedRes(
                type = it.type,
                providerName = it.providerName,
                providerUrl = it.providerUrl,
                title = it.title,
                authorName = it.authorName,
                authorUrl = it.authorUrl,
                thumbnailUrl = it.thumbnailUrl,
                thumbnailWidth = it.thumbnailWidth,
                thumbnailHeight = it.thumbnailHeight,
            )
        }
    }

    @PostMapping(
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    fun createArchive(
        @AuthenticationPrincipal auth: CustomUser,
        @Validated @ModelAttribute form: ArchiveFormReq
    ): ResponseEntity<ArchiveRes> {
        val blocks: List<BlockInfoDto> = form.blocks.map { block ->
            val payload = archiveService.validateAndGetPayload(block)
            BlockInfoDto(position = block.position, type = block.type, payload = payload)

        }
        val archive =
            archiveService.saveArchive(
                userId = auth.getId(),
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

    @GetMapping("/groups/detail")
    @PreAuthorize("isAuthenticated()")
    fun getArchives(
        @AuthenticationPrincipal user: CustomUser,
        @Min(0) @RequestParam("page") page: Int,
    ): ResponseEntity<SliceRes<GroupDetailRes>> {
        val pages = groupService.listGroupsByUser(user.getId(), page, PageSize.MAIN_NUM_PAGES)
        val groups = pages.content.map { GroupSummaryDto(it.id!!, it.name) }
        val groupsDetails = groups.map { groupService.getGroupDetails(it) }
        val sliceMeta = SliceMeta.of(pages)
        val content = groupsDetails.map { group ->
            GroupDetailRes(
                id = group.id,
                name = group.name,
                thumbnails = group.thumbnails.map { it.filePath },
                totalElements = group.totalElements
            )
        }
        return ResponseEntity.ok(SliceRes(meta = sliceMeta, content = content))
    }

    @GetMapping("/groups")
    @PreAuthorize("isAuthenticated()")
    fun listGroups(@AuthenticationPrincipal user: CustomUser): ResponseEntity<ListRes<GroupSummaryRes>> {
        val groups = groupService.listAllGroupsByUser(user.getId())
        return ResponseEntity.ok(ListRes(groups.map {
            GroupSummaryRes(
                id = it.id!!,
                name = it.name
            )
        }))
    }

    @GetMapping("/tags")
    @PreAuthorize("isAuthenticated()")
    fun listTags(@AuthenticationPrincipal user: CustomUser): ResponseEntity<ListRes<String>> {
        val tags = tagService.listUserTags(user.getId())
        return ResponseEntity.ok(ListRes(content = tags.map { it.slug }))
    }

    @PostMapping("/group")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    fun createPackage(
        @AuthenticationPrincipal user: CustomUser,
        @Validated @RequestBody body: GroupReq
    ) {
        groupService.createGroup(body.groupName, user.getId())
    }
}