package net.artux.ailingo.server.model

data class ImageInfo(
    val filename: String,
    val name: String,
    val mime: String,
    val extension: String,
    val url: String
)