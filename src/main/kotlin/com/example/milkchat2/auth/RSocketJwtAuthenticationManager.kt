package com.example.milkchat2.auth

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import reactor.core.publisher.Mono
import org.springframework.core.convert.converter.Converter
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter


class RSocketJwtAuthenticationManager(
        private val jwtDecoder: ReactiveJwtDecoder,
): ReactiveAuthenticationManager {
    private var jwtAuthenticationConverter: Converter<Jwt, out Mono<out AbstractAuthenticationToken>> =
            ReactiveJwtAuthenticationConverterAdapter(JwtAuthenticationConverter())

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return Mono.justOrEmpty(authentication)
                .filter{ it is BearerTokenAuthenticationToken }
                .cast(BearerTokenAuthenticationToken::class.java)
                .map{ it.token }
                .flatMap { jwtDecoder.decode(it) }
                .flatMap(jwtAuthenticationConverter::convert)
                .cast(Authentication::class.java)
                .onErrorMap{ e -> throw Exception("invalid token")}
    }

}