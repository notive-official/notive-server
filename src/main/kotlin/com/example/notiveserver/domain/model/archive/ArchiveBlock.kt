package com.example.notiveserver.domain.model.archive

import com.example.notiveserver.common.enums.BlockType
import com.example.notiveserver.domain.model.Timestamped
import jakarta.persistence.*
import org.hibernate.annotations.Check
import org.hibernate.annotations.DiscriminatorFormula
import org.hibernate.annotations.Formula
import org.hibernate.annotations.UpdateTimestamp
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
    open var position: Int,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 15)
    open var type: BlockType,

    @UpdateTimestamp
    @Column(
        name = "updated_at",
        nullable = false,
        columnDefinition = "DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)"
    )
    var updatedAt: LocalDateTime? = null,

    @Formula("coalesce(path, url, content)")
    var payload: String,

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