package com.example.notiveserver.api.controller

import com.example.notiveserver.api.dto.archive.response.ArchiveSummaryRes
import com.example.notiveserver.api.dto.common.ListRes
import com.example.notiveserver.api.dto.common.SliceMeta
import com.example.notiveserver.api.dto.common.SliceRes
import com.example.notiveserver.api.dto.group.GroupDetailRes
import com.example.notiveserver.api.dto.group.GroupReq
import com.example.notiveserver.api.dto.group.GroupSummaryRes
import com.example.notiveserver.application.archive.ArchiveSearchService
import com.example.notiveserver.application.archive.ArchiveService
import com.example.notiveserver.application.archive.GroupService
import com.example.notiveserver.application.archive.TagService
import com.example.notiveserver.common.policy.PageSize
import jakarta.validation.constraints.Min
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/group")
class GroupController(
    private val groupService: GroupService,
    private val archiveService: ArchiveService,
    private val tagService: TagService,
    private val archiveSearchService: ArchiveSearchService,
) {

    @GetMapping("/metas")
    fun listDetailedGroups(
        @Min(0) @RequestParam("page") page: Int,
    ): ResponseEntity<SliceRes<GroupDetailRes>> {
        val pages = groupService.listGroupsByUser(page, PageSize.MAIN)
        val groupsDetails = pages.content.map { groupService.getDetailedGroup(it) }
        val sliceMeta = SliceMeta.of(pages)
        val content = groupsDetails.map { group ->
            GroupDetailRes.of(group)
        }
        return ResponseEntity.ok(SliceRes(meta = sliceMeta, content = content))
    }

    @GetMapping("/metas/{groupId}")
    fun getGroupMeta(
        @PathVariable groupId: UUID,
    ): ResponseEntity<GroupSummaryRes> {
        val group = groupService.getGroup(groupId)
        return ResponseEntity.ok(GroupSummaryRes(id = group.id, name = group.name))
    }

    @GetMapping("/names")
    fun listGroups(): ResponseEntity<ListRes<GroupSummaryRes>> {
        val groups = groupService.listAllGroupsByUser()
        return ResponseEntity.ok(ListRes(groups.map {
            GroupSummaryRes(
                id = it.id,
                name = it.name
            )
        }))
    }

    @GetMapping("/{groupId}/archives")
    fun listUserArchivesByGroup(
        @Min(0) @RequestParam("page") page: Int,
        @PathVariable groupId: UUID,
    ): ResponseEntity<SliceRes<ArchiveSummaryRes>> {
        val pages = archiveSearchService.listArchivesByGroup(
            pageable = PageRequest.of(
                page, PageSize.SUB,
                Sort.by(Sort.Direction.DESC, "createdAt")
            ),
            groupId = groupId
        )
        val sliceMeta = SliceMeta.of(pages)
        val content = pages.content.map { archive ->
            val writer = archive.writer
            val tags = tagService.listTagsByArchive(archiveId = archive.id)
            ArchiveSummaryRes.of(archive = archive, tags = tags, writer = writer)
        }
        return ResponseEntity.ok(SliceRes(meta = sliceMeta, content = content))
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createGroup(
        @Validated @RequestBody body: GroupReq
    ) {
        groupService.createGroup(body.groupName)
    }

    @PutMapping("/{groupId}")
    @ResponseStatus(HttpStatus.OK)
    fun updateGroup(
        @Validated @RequestBody body: GroupReq,
        @PathVariable groupId: UUID,
    ) {
        groupService.updateGroup(groupId, body.groupName)
    }

    @DeleteMapping("/{groupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteGroup(
        @PathVariable groupId: UUID,
    ) {
        groupService.deleteGroupWithArchives(groupId)
    }
}