package com.example.notiveserver.domain.model.archive

import com.example.notiveserver.domain.model.user.Authority
import jakarta.persistence.*

@Entity
@Table(name = "archive")
class Archive(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "name", nullable = false, length = 50)
    var title: String,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "archive_tag",
        joinColumns = [JoinColumn(name = "archive_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    var tags: MutableSet<Authority> = mutableSetOf()
)