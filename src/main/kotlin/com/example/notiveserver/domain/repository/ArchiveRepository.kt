package com.example.notiveserver.domain.repository

import com.example.notiveserver.domain.model.archive.Archive
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ArchiveRepository : JpaRepository<Archive, Long>
