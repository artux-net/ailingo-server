package net.artux.ailingo.server.model

data class ImageUploadRequest(
    val image: String,
    val name: String? = null,
    val expiration: Int? = null
)