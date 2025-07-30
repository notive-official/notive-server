package com.example.notiveserver.application.archive

import com.example.notiveserver.common.util.SlugUtil
import com.example.notiveserver.domain.model.archive.Tag
import com.example.notiveserver.domain.repository.TagRepository
import com.example.notiveserver.domain.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class TagService(
    private val tagRepository: TagRepository,
    private val userRepository: UserRepository
) {
    fun listUserTags(userId: UUID): List<Tag> =
        tagRepository.findDistinctByArchivesWriterIdOrderBySlugAsc(userId)

    fun getOrSave(rawTags: List<String>): List<Tag> {
        return rawTags
            .mapNotNull { it.trim().takeIf(String::isNotBlank) }
            .map { tag ->
                val slug = SlugUtil.slugify(tag)
                tagRepository.findBySlug(slug) ?: tagRepository.save(Tag(slug = slug))
            }
    }
}