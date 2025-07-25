package com.example.notiveserver.common.enums

import com.example.notiveserver.domain.model.archive.*

enum class BlockType(
    val creator: (pos: Int, arc: Archive, payload: String) -> ArchiveBlock
) {

    PARAGRAPH({ pos, arc, payload ->
        TextBlock(
            position = pos,
            type = PARAGRAPH,
            archive = arc,
            payload = payload
        )
    }),
    H1({ pos, arc, payload ->
        TextBlock(
            position = pos,
            type = H1,
            archive = arc,
            payload = payload
        )
    }),
    H2({ pos, arc, payload ->
        TextBlock(
            position = pos,
            type = H2,
            archive = arc,
            payload = payload
        )
    }),
    H3({ pos, arc, payload ->
        TextBlock(
            position = pos,
            type = H3,
            archive = arc,
            payload = payload
        )
    }),

    IMAGE({ pos, arc, payload ->
        ImageBlock(
            position = pos,
            type = IMAGE,
            archive = arc,
            payload = payload
        )
    }),
    LINK({ pos, arc, payload ->
        LinkBlock(
            position = pos,
            type = LINK,
            archive = arc,
            payload = payload
        )
    });
}

