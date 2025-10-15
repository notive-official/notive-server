package com.example.notiveserver.common.enums

enum class ImageCategory(val getDirectoryPath: () -> String) {
    ARCHIVE_THUMBNAIL({ "/archive/thumbnail" }),
    ARCHIVE_BLOCK({ "/archive/block" }),
    PROFILE({ "/profile" }),
}