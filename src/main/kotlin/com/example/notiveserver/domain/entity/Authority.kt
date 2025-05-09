package com.example.notiveserver.domain.entity

import jakarta.persistence.*

@Entity
@Table(name = "authority")
class Authority (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "name", nullable = false, unique = true, length = 50)
    var name: String,
)
