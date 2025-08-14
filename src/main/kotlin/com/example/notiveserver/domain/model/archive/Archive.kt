package com.example.notiveserver.domain.model.archive

import com.example.notiveserver.common.enums.ArchiveType
import com.example.notiveserver.domain.model.Timestamped
import com.example.notiveserver.domain.model.user.User
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import org.hibernate.annotations.UuidGenerator
import java.util.*

@Entity
@Table(name = "archives")
@SQLDelete(sql = "UPDATE archive SET deleted_at = CURRENT_TIMESTAMP where id = ?")
@SQLRestriction("deleted_at is NULL")
class Archive(

    @Id
    @GeneratedValue
    @UuidGenerator(
        style = UuidGenerator.Style.TIME
    )
    @Column(columnDefinition = "BINARY(16)")
    var id: UUID? = null,

    @Column(name = "thumbnail_path")
    var thumbnailPath: String? = null,

    @Column(name = "title", nullable = false, length = 64)
    var title: String,

    @Column(name = "is_public", nullable = false)
    var isPublic: Boolean,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    open val type: ArchiveType,

    @Column(name = "is_replicable", nullable = false)
    var isReplicable: Boolean,

    @Column(name = "summary", nullable = false, length = 100)
    val summary: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    var group: Group,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    var writer: User,

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinTable(
        name = "archive_tag",
        joinColumns = [JoinColumn(name = "archive_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    var tags: MutableSet<Tag> = mutableSetOf()
) : Timestamped() {

    companion object {
        fun create(
            thumbnailPath: String?,
            title: String,
            tags: List<Tag>,
            isPublic: Boolean,
            type: ArchiveType,
            isReplicable: Boolean,
            summary: String,
            group: Group,
            writer: User
        ): Archive {
            return Archive(
                thumbnailPath = thumbnailPath,
                title = title,
                isPublic = isPublic,
                type = type,
                isReplicable = isReplicable,
                summary = summary,
                group = group,
                writer = writer,
                tags = tags.toMutableSet()
            )
        }
    }
}