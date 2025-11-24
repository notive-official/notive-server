package com.example.notiveserver.api.bookmark

import com.example.notiveserver.application.bookmark.BookmarkService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/bookmark")
class BookmarkController(
    private val bookmarkService: BookmarkService,
) {
    @PostMapping("/{archiveId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun createBookmark(
        @PathVariable archiveId: UUID,
    ) {
        bookmarkService.markArchive(archiveId = archiveId)
    }

    @DeleteMapping("/{archiveId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteBookmark(
        @PathVariable archiveId: UUID,
    ) {
        bookmarkService.unmarkArchive(archiveId = archiveId)
    }
}