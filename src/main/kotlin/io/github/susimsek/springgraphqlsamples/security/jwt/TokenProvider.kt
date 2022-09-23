package io.github.susimsek.springgraphqlsamples.security.jwt

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.jackson.io.JacksonSerializer
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.security.Key
import java.util.Date

private const val AUTHORITIES_KEY = "auth"

private const val INVALID_JWT_TOKEN = "Invalid JWT token."

@Component
@EnableConfigurationProperties(TokenProperties::class)
class TokenProvider(
    tokenProperties: TokenProperties
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private var key: Key? = null

    private var jwtParser: JwtParser? = null

    private var tokenValidityInMilliseconds: Long = 0

    init {
        val keyBytes: ByteArray
        val secret = tokenProperties.base64Secret
        keyBytes = Decoders.BASE64.decode(secret)
        this.key = Keys.hmacShaKeyFor(keyBytes)
        this.jwtParser = Jwts.parserBuilder().setSigningKey(key).build()
        this.tokenValidityInMilliseconds = 1000 * tokenProperties.tokenValidityInSeconds
    }

    fun createToken(authentication: Authentication): String {
        val authorities = authentication.authorities.asSequence()
            .map { it.authority }
            .joinToString(separator = ",")

        val now = Date().time
        val validity = Date(now + this.tokenValidityInMilliseconds)

        return Jwts.builder()
            .setSubject(authentication.name)
            .claim(AUTHORITIES_KEY, authorities)
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(validity)
            .serializeToJsonWith(JacksonSerializer())
            .compact()
    }

    fun getAuthentication(token: String): Authentication {
        val claims = jwtParser?.parseClaimsJws(token)?.body

        val authorities = claims?.get(AUTHORITIES_KEY)?.toString()?.splitToSequence(",")
            ?.filter { it.trim().isNotEmpty() }?.mapTo(mutableListOf()) { SimpleGrantedAuthority(it) }

        val principal = User(claims?.subject, "", authorities)

        return UsernamePasswordAuthenticationToken(principal, token, authorities)
    }

    fun validateToken(authToken: String): Boolean {
        try {
            jwtParser?.parseClaimsJws(authToken)
            return true
        } catch (e: ExpiredJwtException) {

            log.trace(INVALID_JWT_TOKEN, e)
        } catch (e: UnsupportedJwtException) {

            log.trace(INVALID_JWT_TOKEN, e)
        } catch (e: MalformedJwtException) {

            log.trace(INVALID_JWT_TOKEN, e)
        } catch (e: SignatureException) {

            log.trace(INVALID_JWT_TOKEN, e)
        } catch (e: IllegalArgumentException) {
            log.error("Token validation error {}", e.message)
        }

        return false
    }
}
