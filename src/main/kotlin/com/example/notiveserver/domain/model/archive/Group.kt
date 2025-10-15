package com.example.notiveserver.domain.model.archive

import com.example.notiveserver.domain.model.Timestamped
import com.example.notiveserver.domain.model.user.User
import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import org.hibernate.annotations.UuidGenerator
import java.util.*

@Entity
@Table(name = "packages", uniqueConstraints = [UniqueConstraint(columnNames = ["name", "user_id"])])
@SQLDelete(sql = "UPDATE group SET deleted_at = CURRENT_TIMESTAMP where id = ?")
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