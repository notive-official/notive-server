package com.example.notiveserver.application.archive

import com.example.notiveserver.common.util.SlugUtil
import com.example.notiveserver.domain.model.archive.Tag
import com.example.notiveserver.domain.repository.TagRepository
import com.example.notiveserver.domain.repository.UserRepository
import com.example.notiveserver.infrastructure.security.SecurityUtils
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class TagService(
    private val tagRepository: TagRepository,
    private val userRepository: UserRepository
) {
    @PreAuthorize("isAuthenticated()")
    fun listTagsOwnedByUser(): List<Tag> {
        val userId = SecurityUtils.currentUserId
        return tagRepository.findDistinctByArchivesWriterIdOrderBySlugAsc(userId)
    }

    @PreAuthorize("isAuthenticated()")
    fun getOrSave(rawTags: List<String>): List<Tag> {
        return rawTags
            .mapNotNull { it.trim().takeIf(String::isNotBlank) }
            .map { tag ->
                val slug = SlugUtil.slugify(tag)
                tagRepository.findBySlug(slug) ?: tagRepository.save(Tag(slug = slug))
            }
    }
}