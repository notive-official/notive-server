package com.example.notiveserver.domain.model.user

import com.example.notiveserver.domain.model.Timestamped
import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import org.hibernate.annotations.UuidGenerator
import java.util.*

@Entity
@Table(name = "users")
@SQLDelete(sql = "UPDATE user SET deleted_at = CURRENT_TIMESTAMP where id = ?")
@SQLRestriction("deleted_at is NULL")
class User(
    @Id
    @GeneratedValue
    @UuidGenerator(
        style = UuidGenerator.Style.TIME
    )
    @Column(columnDefinition = "BINARY(16)")
    var id: UUID? = null,

    @Column(name = "name", nullable = false, length = 100) // 최대 30자
    var name: String,

    @Column(name = "email", nullable = false, length = 200, updatable = false)
    var email: String,

    @Column(name = "nickname", length = 30, unique = true) // 최대 10자
    var nickname: String,

    @Column(name = "profile_image")
    var profileImage: String? = null,

    @Column(name = "social_id", nullable = false, unique = true, updatable = false)
    var socialId: String,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_authority",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "authority_id")]
    )
    var authorities: MutableSet<Authority> = mutableSetOf()
) : Timestamped()