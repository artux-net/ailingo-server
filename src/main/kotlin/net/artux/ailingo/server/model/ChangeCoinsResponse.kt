package net.artux.ailingo.server.model

data class ChangeCoinsResponse(
    var success: Boolean = false,
    var message: String? = null,
    var newBalance: Int = 0
)
