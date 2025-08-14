package com.example.notiveserver.api.controller

import com.example.notiveserver.api.dto.common.ListRes
import com.example.notiveserver.api.dto.common.SliceMeta
import com.example.notiveserver.api.dto.common.SliceRes
import com.example.notiveserver.api.dto.group.GroupDetailRes
import com.example.notiveserver.api.dto.group.GroupReq
import com.example.notiveserver.api.dto.group.GroupSummaryRes
import com.example.notiveserver.application.archive.GroupService
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
        val groupsDetails = pages.content.map { groupService.getGroupDetails(it) }
        val sliceMeta = SliceMeta.of(pages)
        val content = groupsDetails.map { group ->
            GroupDetailRes.of(group)
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