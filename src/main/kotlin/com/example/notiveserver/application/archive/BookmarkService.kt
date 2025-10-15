package com.example.notiveserver.application.archive

import com.example.notiveserver.common.exception.ArchiveException
import com.example.notiveserver.common.exception.code.ArchiveErrorCode
import com.example.notiveserver.domain.model.archive.Bookmark
import com.example.notiveserver.domain.repository.ArchiveRepository
import com.example.notiveserver.domain.repository.BookmarkRepository
import com.example.notiveserver.domain.repository.UserRepository
import com.example.notiveserver.infrastructure.security.SecurityUtils
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrDefault
import kotlin.jvm.optionals.getOrElse

@Service
class BookmarkService(
    private val bookmarkRepository: BookmarkRepository,
    private val userRepository: UserRepository,
    private val archiveRepository: ArchiveRepository
) {
    fun isMarked(archiveId: UUID): Boolean {
        if (!SecurityUtils.isAuthenticated) return false
        val userId = SecurityUtils.currentUserId
        return bookmarkRepository
            .findByArchiveIdAndUserId(archiveId, userId)
            .map { it.isMarked }
            .orElse(false)
    }

    @PreAuthorize("isAuthenticated()")
    fun markArchive(archiveId: UUID) {
        val userId = SecurityUtils.currentUserId
        val bookmark = bookmarkRepository.findByArchiveIdAndUserId(archiveId, userId).getOrDefault(
            Bookmark.create(
                isMarked = false,
                user = userRepository.getReferenceById(userId),
                archive = archiveRepository.getReferenceById(archiveId)
            )
        )
        if (bookmark.isMarked) {
            throw ArchiveException(ArchiveErrorCode.BOOKMARK_ALREADY_EXISTS)
        }
        bookmark.isMarked = true
        bookmarkRepository.save(bookmark)
    }

    @PreAuthorize("isAuthenticated()")
    fun unmarkArchive(archiveId: UUID) {
        val userId = SecurityUtils.currentUserId
        val bookmark = bookmarkRepository.findByArchiveIdAndUserId(archiveId, userId).getOrElse {
            throw ArchiveException(ArchiveErrorCode.BOOKMARK_NOT_FOUND)
        }
        if (!bookmark.isMarked) {
            throw ArchiveException(ArchiveErrorCode.BOOKMARK_NOT_FOUND)
        }
        bookmark.isMarked = false
        bookmarkRepository.save(bookmark)
    }
}