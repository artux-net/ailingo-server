package net.artux.ailingo.server.jwt.model

data class RefreshTokenResponse(
    val accessToken: String,
    val refreshToken: String
)