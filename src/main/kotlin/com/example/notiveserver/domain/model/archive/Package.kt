package com.example.notiveserver.domain.model.archive

import com.example.notiveserver.domain.model.user.User
import jakarta.persistence.*

@Entity
@Table(name = "package")
class Package(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "name", nullable = false, length = 50)
    var name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user", nullable = false)
    var user: User,
)