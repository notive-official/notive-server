package com.example.notiveserver.domain.model.archive

import jakarta.persistence.*

@Entity
@Table(name = "tag")
class Tag(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "name", nullable = false, length = 50)
    var name: String,
)