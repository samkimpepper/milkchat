package com.example.milkchat2.auth

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.stereotype.Component
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date

class BearerToken(val value: String) : AbstractAuthenticationToken(AuthorityUtils.NO_AUTHORITIES) {
    override fun getCredentials() = value

    override fun getPrincipal() = value
}

@Component
class JwtSupport(
        @Value("\${jwt.secret}")
        private val key: ByteArray
) {
    private val jwtKey = Keys.hmacShaKeyFor(key)
    private val parser = Jwts.parserBuilder().setSigningKey(jwtKey).build()

    fun generate(email: String): BearerToken {
        var builder = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
                .signWith(jwtKey)
        return BearerToken(builder.compact())
    }

    fun getUserEmail(token: BearerToken): String {
        return parser.parseClaimsJws(token.value).body.subject
    }

    fun isValid(token: BearerToken, userDetails: UserDetails?): Boolean {
        val claims = parser.parseClaimsJws(token.value).body
        val unexpired = claims.expiration.after(Date.from(Instant.now()))
        return unexpired && (claims.subject == userDetails?.username)
    }
}