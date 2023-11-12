package com.example.milkchat2.auth

import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationManager(
        private val jwtSupport: JwtSupport,
        private val users: ReactiveUserDetailsService,
) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication?): Mono<Authentication> {
        return Mono.justOrEmpty(authentication)
                .filter{auth -> auth is BearerToken }
                .cast(BearerToken::class.java)
                .flatMap { jwt -> mono { validate(jwt) } }
                .onErrorMap { error -> InvalidBearerToken(error.message ?: "Invalid token") }

    }

    private suspend fun validate(token: BearerToken): Authentication {
        println("validate")
        val email = jwtSupport.getUserEmail(token)
        val user = users.findByUsername(email).awaitSingleOrNull()
        if (jwtSupport.isValid(token, user)) {
            return UsernamePasswordAuthenticationToken(user!!.username, user.password, user.authorities)
        }
        throw Exception("Invalid token")
    }
}

class InvalidBearerToken(message: String) : AuthenticationException(message)