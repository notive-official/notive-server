package com.example.notiveserver.domain.tag.model

import com.example.notiveserver.domain.common.model.Timestamped
import com.example.notiveserver.domain.archive.model.Archive
import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.*

@Entity
@Table(name = "tags", uniqueConstraints = [UniqueConstraint(columnNames = ["slug"])])
class Tag(
    @Id
    @GeneratedValue
    @UuidGenerator(
        style = UuidGenerator.Style.TIME
    )
    @Column(columnDefinition = "BINARY(16)")
    var id: UUID? = null,

    @Column(name = "slug", nullable = false, length = 32)
    var slug: String,

    @ManyToMany(mappedBy = "tags")
    @JsonBackReference
    val archives: MutableSet<Archive> = mutableSetOf()
) : Timestamped()