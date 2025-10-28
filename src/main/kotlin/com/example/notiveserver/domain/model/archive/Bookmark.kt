package com.example.notiveserver.domain.model.archive

import com.example.notiveserver.domain.model.Timestamped
import com.example.notiveserver.domain.model.user.User
import jakarta.persistence.*
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime


@Entity
@Table(name = "bookmarks")
class Bookmark(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archive_id", nullable = false)
    var archive: Archive,

    @Column(name = "is_marked", nullable = false)
    var isMarked: Boolean,

    @UpdateTimestamp
    @Column(
        name = "updated_at",
        nullable = false,
        columnDefinition = "DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)"
    )
    var updatedAt: LocalDateTime? = null,

    ) : Timestamped() {
    companion object {
        fun create(isMarked: Boolean, user: User, archive: Archive): Bookmark = Bookmark(
            isMarked = isMarked,
            user = user,
            archive = archive,
        )
    }
}