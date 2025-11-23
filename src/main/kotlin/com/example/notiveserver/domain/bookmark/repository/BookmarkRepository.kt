package com.example.notiveserver.domain.bookmark.repository

import com.example.notiveserver.domain.bookmark.model.Bookmark
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BookmarkRepository : JpaRepository<Bookmark, UUID> {
    fun findByArchiveIdAndUserId(archiveId: UUID, userId: UUID): Optional<Bookmark>
    fun findByArchiveId(archiveId: UUID): Optional<Bookmark>

    @Query(
        """
        SELECT b FROM Bookmark b
        JOIN FETCH b.archive a
        WHERE b.user.id = :userId
          AND b.isMarked = true
          AND (a.deletedAt IS NULL)
        ORDER BY b.updatedAt DESC
        """
    )
    fun findActiveBookmarkByUserIdOrderByUpdatedAtDesc(
        userId: UUID,
        pageable: Pageable
    ): Page<Bookmark>
}
