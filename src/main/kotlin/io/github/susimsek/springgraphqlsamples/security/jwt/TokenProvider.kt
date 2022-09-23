package io.github.susimsek.springgraphqlsamples.security.jwt

import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jose.shaded.json.parser.ParseException
import com.nimbusds.jose.util.Base64
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.util.Date


private const val AUTHORITIES_KEY = "auth"

private const val INVALID_JWT_TOKEN = "Invalid JWT token."

@Component
@EnableConfigurationProperties(TokenProperties::class)
class TokenProvider(
    tokenProperties: TokenProperties
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private lateinit var keyBytes: ByteArray

    private var tokenValidityInMilliseconds: Long = 0

    init {
        val secret = tokenProperties.base64Secret
        this.keyBytes = Base64(secret).decode()
        this.tokenValidityInMilliseconds = 1000 * tokenProperties.tokenValidityInSeconds
    }

    fun createToken(authentication: Authentication): String {
        val authorities = authentication.authorities.asSequence()
            .map { it.authority }
            .joinToString(separator = ",")

        val now = Date().time
        val validity = Date(now + this.tokenValidityInMilliseconds)

        val header = JWSHeader.Builder(JWSAlgorithm.HS512)
            .type(JOSEObjectType.JWT)
            .build()

        val payload = JWTClaimsSet.Builder()
            .subject(authentication.name)
            .claim(AUTHORITIES_KEY, authorities)
            .expirationTime(validity)
            .build()

        val signedJWT = SignedJWT(header, payload)
        signedJWT.sign(MACSigner(keyBytes))
        return signedJWT.serialize()
    }

    fun getAuthentication(token: String): Authentication {
        val claims = SignedJWT.parse(token).jwtClaimsSet

        val authorities = claims.getClaim(AUTHORITIES_KEY)?.toString()?.splitToSequence(",")
            ?.filter { it.trim().isNotEmpty() }?.mapTo(mutableListOf()) { SimpleGrantedAuthority(it) }

        val principal = User(claims?.subject, "", authorities)

        return UsernamePasswordAuthenticationToken(principal, token, authorities)
    }

    fun validateToken(authToken: String): Boolean {
        try {
            SignedJWT.parse(authToken)
                .verify(MACVerifier(keyBytes))
            return true
        } catch (e: ParseException) {
            log.trace(INVALID_JWT_TOKEN, e)
        } catch (e: JOSEException) {
            log.error("Token validation error {}", e.message)
        }

        return false
    }

}
