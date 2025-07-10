package com.example.notiveserver.domain.model

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.CreationTimestamp
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class Timestamped {

    @CreationTimestamp
    @Column(
        name = "created_at",
        nullable = false,
        updatable = false,
        columnDefinition = "DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)"
    )
    var createdAt: LocalDateTime? = null

    @Column(
        name = "deleted_at",
        columnDefinition = "DATETIME(3)"
    )
    var deletedAt: LocalDateTime? = null
}