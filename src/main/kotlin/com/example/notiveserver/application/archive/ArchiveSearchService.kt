package com.example.notiveserver.application.archive

import com.example.notiveserver.application.archive.dto.ArchiveSearchCondition
import com.example.notiveserver.application.archive.dto.ArchiveSummaryDto
import com.example.notiveserver.application.archive.dto.BookmarkDto
import com.example.notiveserver.common.enums.ArchiveType
import com.example.notiveserver.domain.archive.repository.ArchiveRepository
import com.example.notiveserver.domain.bookmark.repository.BookmarkRepository
import com.example.notiveserver.infrastructure.security.SecurityCurrentUserProvider
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import java.util.*

@Service
class ArchiveSearchService(
    private val archiveRepository: ArchiveRepository,
    private val currentUser: SecurityCurrentUserProvider,
    private val bookmarkRepository: BookmarkRepository,
) {
    fun searchArchives(
        cond: ArchiveSearchCondition,
        pageable: Pageable,
    ): Page<ArchiveSummaryDto> {
        val pages = archiveRepository.searchArchivesWithWriter(cond, pageable)
        return pages.map { archive ->
            val writer = archive.writer
            ArchiveSummaryDto.of(archive, writer)
        }
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    fun listArchivesByUser(
        pageable: Pageable,
        archiveType: ArchiveType?
    ): Page<ArchiveSummaryDto> {
        val userId = currentUser.id()
        val pages = if (archiveType != null) {
            archiveRepository.findByWriterIdAndType(
                userId,
                archiveType,
                pageable
            )
        } else {
            archiveRepository.findByWriterId(userId, pageable)
        }

        return pages.map { archive ->
            val writer = archive.writer
            ArchiveSummaryDto.of(archive, writer)
        }
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and @accessManager.isGroupOwner(#groupId)")
    fun listArchivesByGroup(
        pageable: Pageable,
        groupId: UUID
    ): Page<ArchiveSummaryDto> {
        val pages = archiveRepository.findByGroupId(
            groupId,
            pageable
        )
        return pages.map { archive ->
            val writer = archive.writer
            ArchiveSummaryDto.of(archive, writer)
        }
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    fun listBookmarkedArchivesByUser(pageable: Pageable): Page<BookmarkDto> {
        val userId = currentUser.id()
        val pages =
            bookmarkRepository.findByUserIdAndIsMarkedTrueAndArchiveDeletedAtIsNull(
                userId,
                pageable
            )
        return pages.map { bookmark ->
            BookmarkDto.of(
                bookmark,
                ArchiveSummaryDto.of(bookmark.archive, bookmark.archive.writer)
            )
        }
    }
}
