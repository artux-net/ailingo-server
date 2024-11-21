package net.artux.ailingo.server.configuration.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import net.artux.ailingo.server.entity.RefreshTokenEntity
import net.artux.ailingo.server.entity.user.UserEntity
import net.artux.ailingo.server.repositories.RefreshTokenRepository
import net.artux.ailingo.server.repositories.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.security.Key
import java.time.Instant
import java.util.*


@Service
class JwtUtil(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    @Value("\${jwt.secret}") private val secretKey: String,
    @Value("\${jwt.expiration}") private val jwtExpiration: Long,
    @Value("\${jwt.refresh-token.expiration}") private val refreshTokenExpiration: Long
) {
    private val logger: Logger = LoggerFactory.getLogger(JwtUtil::class.java)

    fun generateRefreshToken(userDetails: UserDetails): String {
        val user: UserEntity = userRepository.findByLogin(userDetails.username).orElseThrow()

        // Инвалидируем старый refresh token
        refreshTokenRepository.deleteByUser(user)

        val refreshToken = createRefreshToken(HashMap(), userDetails.username)
        val expiryDate = Instant.now().plusMillis(refreshTokenExpiration)

        // Сохраняем новый refresh token
        refreshTokenRepository.save(RefreshTokenEntity(user, refreshToken, expiryDate))

        return refreshToken
    }

    fun refreshToken(refreshToken: String): String? {
        try {
            val username = extractUsername(refreshToken)
            if (username != null) {
                val user: UserEntity = userRepository.findByLogin(username).orElseThrow()
                val refreshTokenEntity = refreshTokenRepository.findByUser(user).orElse(null)

                // Проверяем refresh token в базе
                if (refreshTokenEntity != null && refreshToken == refreshTokenEntity.token && !refreshTokenEntity.isExpired) {
                    val accessToken = generateToken(user)
                    refreshTokenRepository.delete(refreshTokenEntity)
                    return accessToken
                }
            }
        } catch (e: JwtException) {
            logger.error(e.message)
        }
        return null
    }

    private fun createRefreshToken(extraClaims: Map<String, Any>, username: String): String {
        return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + refreshTokenExpiration)) //2 дня для refresh token
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact()
    }

    fun extractUsername(token: String): String? {
        return extractClaim(token, Claims::getSubject)
    }

    fun extractExpiration(token: String): Date {
        return extractClaim(token, Claims::getExpiration)
    }

    fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token)
        return claimsResolver(claims)
    }

    private fun extractAllClaims(token: String): Claims {
        try {
            return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .body
        } catch (e: JwtException) {
            throw IllegalArgumentException("Invalid token", e)
        }
    }

    fun generateToken(userDetails: UserDetails): String {
        return createToken(HashMap(), userDetails.username)
    }

    fun createToken(
        extraClaims: Map<String, Any>,
        username: String
    ): String {
        return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + jwtExpiration)) // 15 минут для access token
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact()
    }

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return (username.equals(userDetails.username) && !isTokenExpired(token))
    }

    private fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }

    private fun getSignInKey(): Key {
        val keyBytes = Decoders.BASE64.decode(secretKey)
        return Keys.hmacShaKeyFor(keyBytes)
    }
}