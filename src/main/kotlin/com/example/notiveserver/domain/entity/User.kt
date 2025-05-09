package com.example.notiveserver.domain.entity

import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Entity
@Table(name = "user")
@SQLDelete(sql = "UPDATE user SET deleted_at = CURRENT_TIMESTAMP where id = ?")
@SQLRestriction("deleted_at is NULL")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "name", nullable = false, length = 100) // 최대 30자
    var name: String,

    @Column(name = "email", nullable = false, length = 200, updatable = false)
    var email: String,

    @Column(name = "nickname", length = 30, unique = true) // 최대 10자
    var nickname: String,

    @Column(name = "profile_image")
    var profileImage: String? = null,

    @Column(name = "social_id", nullable = false, unique = true)
    var socialId: String,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_authority",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "authority_id")]
    )
    var authorities: MutableSet<Authority> = mutableSetOf()
) : Timestamped()