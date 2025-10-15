package com.example.notiveserver.domain.model.archive

import com.example.notiveserver.common.enums.BlockType
import com.example.notiveserver.domain.model.Timestamped
import jakarta.persistence.*
import org.hibernate.annotations.Check
import org.hibernate.annotations.DiscriminatorFormula
import org.hibernate.annotations.Formula
import java.time.LocalDateTime

@Entity
@Table(name = "archive_blocks")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorFormula(
    "CASE " +
            "  when type in ('PARAGRAPH','H1','H2','H3') then 'TEXT' " +
            "  WHEN type IN ('IMAGE') THEN 'IMAGE' " +
            "  WHEN type IN ('LINK') THEN 'LINK' " +
            "  else 'TEXT' " +
            "END"
)
@Check(
    constraints = """
      (type in ('PARAGRAPH','H1','H2','H3') AND content IS NOT NULL) OR
      (type = 'IMAGE' AND path IS NOT NULL) OR
      (type = 'LINK' AND url IS NOT NULL)
    """
)
class ArchiveBlock(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long? = null,

    @Column(name = "position", nullable = false)
    open val position: Int,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 15)
    open val type: BlockType,

    @Column(
        name = "updated_at",
        nullable = false,
        updatable = false,
        insertable = false,
        columnDefinition = """
        DATETIME(3)
        NOT NULL
        DEFAULT CURRENT_TIMESTAMP(3)
        ON UPDATE CURRENT_TIMESTAMP(3)
    """
    )
    val updatedAt: LocalDateTime? = null,

    @Formula("coalesce(path, url, content)")
    val payload: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    open val archive: Archive
) : Timestamped() {
    companion object {
        fun create(
            type: BlockType,
            position: Int,
            archive: Archive,
            payload: String
        ): ArchiveBlock = type.creator(position, archive, payload)

    }
}