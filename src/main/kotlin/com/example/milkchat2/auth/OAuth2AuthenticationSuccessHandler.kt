package com.example.milkchat2.auth

import com.example.milkchat2.user.ReactiveUserRepository
import com.example.milkchat2.user.model.User
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import java.net.URI

@Component
class OAuth2AuthenticationSuccessHandler(
    private val jwtSupport: JwtSupport,
    private val userRepository: ReactiveUserRepository,
): ServerAuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(webFilterExchange: WebFilterExchange?, authentication: Authentication?): Mono<Void> {
        return Mono.justOrEmpty(authentication)
                .flatMap { auth ->
                    val oidcUser = auth.principal as OidcUser
                    print("OidcUser: $oidcUser")

                    userRepository.findByEmail(oidcUser.email)
                            .switchIfEmpty(Mono.defer {
                                val user = User(
                                        email = oidcUser.email,
                                        password = null,
                                        nickname = oidcUser.nickName,
                                        provider = "google",
                                )
                                userRepository.save(user)
                            })
                            .flatMap{ user ->
                                val token = jwtSupport.generate(user.email!!)

                                val redirectUri = UriComponentsBuilder.fromUriString("/oauth2/callback")
                                        .queryParam("token", token.value)
                                        .build().toUriString()

                                if (webFilterExchange != null) {
                                    webFilterExchange.exchange.response.statusCode = HttpStatus.FOUND
                                    webFilterExchange.exchange.response.headers.location = URI.create(redirectUri)
                                }

                                Mono.empty<Void>()
                            }


                }
                .then()
    }
}