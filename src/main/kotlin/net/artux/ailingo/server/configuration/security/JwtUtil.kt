package net.artux.ailingo.server.configuration.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import net.artux.ailingo.server.model.refreshtoken.RefreshTokenResponse
import net.artux.ailingo.server.repositories.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.security.Key
import java.util.*
import java.util.concurrent.TimeUnit


@Service
class JwtUtil(
    private val userRepository: UserRepository,
    private val redisTemplate: StringRedisTemplate,
    jwtProperties: JwtProperties
) {
    private val secretKey = jwtProperties.secret
    private val jwtExpiration = jwtProperties.expiration
    private val refreshTokenExpiration = jwtProperties.refreshToken.expiration
    private val logger: Logger = LoggerFactory.getLogger(JwtUtil::class.java)

    fun generateRefreshToken(userDetails: UserDetails): String {
        val username = userDetails.username
        val refreshToken = createRefreshToken(HashMap(), username)
        redisTemplate.opsForValue().set(username, refreshToken, refreshTokenExpiration, TimeUnit.MILLISECONDS)
        logger.info("Refresh token stored: $username")
        return refreshToken
    }

    fun refreshToken(refreshToken: String): RefreshTokenResponse? {
        try {
            val username = extractUsername(refreshToken)
            logger.info("Extracted username: $username")
            if (username != null) {
                val storedRefreshToken = redisTemplate.opsForValue().get(username)
                logger.info("Stored token: $storedRefreshToken")
                if (storedRefreshToken != null && storedRefreshToken == refreshToken && !isTokenExpired(refreshToken)) {
                    val user = userRepository.findByLogin(username).orElseThrow()
                    val accessToken = generateToken(user)
                    val newRefreshToken = generateRefreshToken(user)
                    return RefreshTokenResponse(accessToken, newRefreshToken)
                }
            }
        } catch (e: JwtException) {
            logger.error("Invalid refresh token: {}", e.message)
        }
        logger.info("Refresh token is invalid or expired.")
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