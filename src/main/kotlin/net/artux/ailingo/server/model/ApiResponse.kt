package net.artux.ailingo.server.model

data class ApiResponse<T>(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: T? = null
)