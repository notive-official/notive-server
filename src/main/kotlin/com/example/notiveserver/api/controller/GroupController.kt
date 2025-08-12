package com.example.notiveserver.api.controller

import com.example.notiveserver.api.dto.common.ListRes
import com.example.notiveserver.api.dto.common.SliceMeta
import com.example.notiveserver.api.dto.common.SliceRes
import com.example.notiveserver.api.dto.my.group.GroupDetailRes
import com.example.notiveserver.api.dto.my.group.GroupReq
import com.example.notiveserver.api.dto.my.group.GroupSummaryRes
import com.example.notiveserver.application.archive.GroupService
import com.example.notiveserver.application.archive.dto.GroupSummaryDto
import com.example.notiveserver.common.policy.PageSize
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/group")
class GroupController(
    private val groupService: GroupService,
) {

    @GetMapping("/metas")
    fun getGroupDetails(
        @Min(0) @RequestParam("page") page: Int,
    ): ResponseEntity<SliceRes<GroupDetailRes>> {
        val pages = groupService.listGroupsByUser(page, PageSize.MAIN)
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

    @GetMapping("/names")
    fun listGroups(): ResponseEntity<ListRes<GroupSummaryRes>> {
        val groups = groupService.listAllGroupsByUser()
        return ResponseEntity.ok(ListRes(groups.map {
            GroupSummaryRes(
                id = it.id!!,
                name = it.name
            )
        }))
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createGroup(
        @Validated @RequestBody body: GroupReq
    ) {
        groupService.createGroup(body.groupName)
    }
}