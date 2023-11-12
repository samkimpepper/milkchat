package com.example.milkchat2

import lombok.experimental.UtilityClass
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import reactor.core.publisher.Mono


@UtilityClass
object SecurityUtils {
    fun currentUser(): Mono<String> {
        return ReactiveSecurityContextHolder.getContext()
                .map { it.authentication }
                .flatMap { auth ->
                    val principal = auth.principal
                    when (principal) {
                        is OidcUser -> Mono.just(principal.email)
                        is UserDetails -> Mono.just(principal.username)
                        is String -> Mono.just(principal)
                        else -> Mono.empty()
                    }
                }
//                .map { it.principal }
//                .cast(String::class.java)
    }

    fun curretOAuth2UserInfo(): Mono<OAuth2AuthenticationToken> {
        return ReactiveSecurityContextHolder.getContext()
                .map { it.authentication }
                .cast(OAuth2AuthenticationToken::class.java)
    }
}