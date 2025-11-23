package com.example.notiveserver.domain.user.model

import jakarta.persistence.*

@Entity
@Table(name = "authorities")
class Authority(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "name", nullable = false, unique = true, length = 50)
    var name: String,
)
