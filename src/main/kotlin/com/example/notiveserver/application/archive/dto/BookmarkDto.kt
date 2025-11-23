package com.example.notiveserver.application.archive.dto

import com.example.notiveserver.domain.bookmark.model.Bookmark

data class BookmarkDto(
    val id: Long,
    val isMarked: Boolean,
    val archive: ArchiveSummaryDto,
) {
    companion object {
        fun of(bookmark: Bookmark, archive: ArchiveSummaryDto): BookmarkDto =
            BookmarkDto(
                id = bookmark.id!!,
                isMarked = bookmark.isMarked,
                archive = archive
            )
    }
}
