package net.artux.ailingo.server.model.refreshtoken

data class RefreshTokenResponse(
    val accessToken: String,
    val refreshToken: String
)