package com.example.notiveserver.domain.repository

import com.example.notiveserver.domain.model.archive.ArchiveBlock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ArchiveBlockRepository : JpaRepository<ArchiveBlock, Long>
