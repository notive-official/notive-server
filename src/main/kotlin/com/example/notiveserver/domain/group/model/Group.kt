package com.example.notiveserver.domain.group.model

import com.example.notiveserver.domain.common.model.Timestamped
import com.example.notiveserver.domain.user.model.User
import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import org.hibernate.annotations.UuidGenerator
import java.util.*

@Entity
@Table(name = "packages")
@SQLDelete(sql = "UPDATE packages SET deleted_at = CURRENT_TIMESTAMP where id = ?")
@SQLRestriction("deleted_at is NULL")
class Group(
    @Id
    @GeneratedValue
    @UuidGenerator(
        style = UuidGenerator.Style.TIME
    )
    @Column(columnDefinition = "BINARY(16)")
    var id: UUID? = null,

    @Column(name = "name", nullable = false, length = 32)
    var name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,
) : Timestamped()