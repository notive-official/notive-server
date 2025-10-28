package com.example.notiveserver.domain.model.archive

import com.example.notiveserver.domain.model.Timestamped
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