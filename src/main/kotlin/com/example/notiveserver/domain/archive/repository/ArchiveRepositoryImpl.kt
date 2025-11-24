package com.example.notiveserver.domain.archive.repository

import com.example.notiveserver.application.archive.dto.ArchiveSearchCondition
import com.example.notiveserver.common.querydsl.toOrderSpecifiers
import com.example.notiveserver.domain.archive.model.Archive
import com.example.notiveserver.domain.archive.model.QArchive.archive
import com.example.notiveserver.domain.user.model.QUser.user
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class ArchiveRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : ArchiveRepositoryCustom {

    override fun searchArchivesWithWriter(
        condition: ArchiveSearchCondition,
        pageable: Pageable,
    ): Page<Archive> {
        val content = queryFactory
            .selectFrom(archive)
            .where(
                tagsIn(condition.tags),
                queryInAll(condition.q),
                archive.isPublic
            )
            .join(archive.writer, user).fetchJoin()
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*pageable.toOrderSpecifiers(archive))
            .fetch()

        val count = queryFactory
            .select(archive.count())
            .from(archive)
            .where(
                tagsIn(condition.tags),
                queryInAll(condition.q),
                archive.isPublic
            )
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, count)
    }

    private fun tagsIn(tags: List<String>?): BooleanExpression? =
        tags?.let {
            archive.tags.any().slug.`in`(it)
        }

    private fun queryInAll(q: String?): BooleanExpression? =
        q?.let {
            archive.title.containsIgnoreCase(it)
                .or(archive.summary.containsIgnoreCase(it))
                .or(archive.tags.any().slug.containsIgnoreCase(it))
        }
}
