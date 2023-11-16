package com.example.milkchat2.config

import com.example.milkchat2.auth.JwtAuthenticationManager
import com.example.milkchat2.auth.RSocketJwtAuthenticationManager
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.rsocket.metadata.WellKnownMimeType
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity
import org.springframework.security.config.annotation.rsocket.RSocketSecurity
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter
import org.springframework.security.rsocket.authentication.BearerPayloadExchangeConverter
import org.springframework.security.rsocket.authentication.PayloadExchangeAuthenticationConverter
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor
import org.springframework.security.rsocket.metadata.BearerTokenAuthenticationEncoder
import org.springframework.util.MimeTypeUtils
import reactor.util.retry.Retry
import java.time.Duration
import java.util.*
import javax.crypto.spec.SecretKeySpec


@Configuration
@EnableRSocketSecurity
class RSocketConfig(
        @Value("\${jwt.secret}")
        private val secretKey: ByteArray
) {

    @Bean
    fun getRSocketRequester(strategies: RSocketStrategies): RSocketRequester {
        val authMimeType = MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string)
        val extendedStrategies = strategies.mutate().encoder(BearerTokenAuthenticationEncoder()).build()

        return RSocketRequester.builder()
                .rsocketConnector{ connector ->
                    connector.reconnect(Retry.backoff(10, Duration.ofMillis(500))) }
                .rsocketStrategies(strategies)
                .dataMimeType(MimeTypeUtils.APPLICATION_JSON)
                .tcp("localhost", 8080)
    }

    @Bean
    fun rSocketMessageHandler(strategies: RSocketStrategies?): RSocketMessageHandler? {
        val handler = RSocketMessageHandler()
        handler.argumentResolverConfigurer.addCustomResolver(AuthenticationPrincipalArgumentResolver())
        handler.rSocketStrategies = strategies!!
        return handler
    }

    @Bean
    fun authorization(security: RSocketSecurity,
                      ): PayloadSocketAcceptorInterceptor {
        security.authorizePayload { authorize ->
            authorize.setup().authenticated()
                    .anyExchange().authenticated()
        }.jwt{ jwtSpec -> jwtSpec.authenticationManager(RSocketJwtAuthenticationManager(jwtDecoder())) }

        return security.build()
    }

    fun jwtDecoder(): ReactiveJwtDecoder {
        val secretKeySpec = SecretKeySpec(secretKey, MacAlgorithm.HS256.name)

        return NimbusReactiveJwtDecoder.withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS256)
                .build()
    }

    fun jwtReactiveAuthenticationManager(): ReactiveAuthenticationManager {
        return JwtReactiveAuthenticationManager(jwtDecoder())
    }



    @Bean
    fun authenticationConverter(): PayloadExchangeAuthenticationConverter? {
        return BearerPayloadExchangeConverter()
    }
}