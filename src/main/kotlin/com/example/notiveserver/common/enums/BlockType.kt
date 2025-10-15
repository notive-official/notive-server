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
            content = payload
        )
    }),
    H1({ pos, arc, payload ->
        TextBlock(
            position = pos,
            type = H1,
            archive = arc,
            content = payload
        )
    }),
    H2({ pos, arc, payload ->
        TextBlock(
            position = pos,
            type = H2,
            archive = arc,
            content = payload
        )
    }),
    H3({ pos, arc, payload ->
        TextBlock(
            position = pos,
            type = H3,
            archive = arc,
            content = payload
        )
    }),

    IMAGE({ pos, arc, payload ->
        ImageBlock(
            position = pos,
            type = IMAGE,
            archive = arc,
            path = payload
        )
    }),
    LINK({ pos, arc, payload ->
        LinkBlock(
            position = pos,
            type = LINK,
            archive = arc,
            url = payload
        )
    });
}

