package com.example.notiveserver.domain.archive.repository

import com.example.notiveserver.application.archive.dto.ArchiveSearchCondition
import com.example.notiveserver.domain.archive.model.Archive
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ArchiveRepositoryCustom {
    fun searchArchivesWithWriter(
        condition: ArchiveSearchCondition,
        pageable: Pageable,
    ): Page<Archive>
}