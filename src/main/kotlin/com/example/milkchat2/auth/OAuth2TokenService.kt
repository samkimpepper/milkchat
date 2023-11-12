package com.example.milkchat2.auth

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Service
class OAuth2TokenService(
        private val clientRepository: ServerOAuth2AuthorizedClientRepository,
) {
    fun getAccessToken(exchange: ServerWebExchange): Mono<String> {
        val authentication = exchange.getPrincipal<OAuth2AuthenticationToken>()

        return authentication.flatMap { auth ->
            val clientRegistrationId = auth.authorizedClientRegistrationId
            clientRepository.loadAuthorizedClient<OAuth2AuthorizedClient>(clientRegistrationId, auth, exchange)
                    .map { it.accessToken.tokenValue }
        }
    }
}