package com.example.notiveserver.domain.bookmark.repository

import com.example.notiveserver.domain.bookmark.model.Bookmark
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BookmarkRepository : JpaRepository<Bookmark, UUID> {
    fun findByArchiveIdAndUserId(archiveId: UUID, userId: UUID): Optional<Bookmark>
    fun findByArchiveId(archiveId: UUID): Optional<Bookmark>

    @EntityGraph(attributePaths = ["archive"])
    fun findByUserIdAndIsMarkedTrueAndArchiveDeletedAtIsNull(
        userId: UUID,
        pageable: Pageable
    ): Page<Bookmark>
}
