package com.example.milkchat2.config

import com.example.milkchat2.auth.CustomReactiveOAuth2OidcUserService
import com.example.milkchat2.auth.JwtAuthenticationConverter
import com.example.milkchat2.auth.JwtAuthenticationManager
import com.example.milkchat2.auth.OAuth2AuthenticationSuccessHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.OAuth2LoginSpec
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import org.springframework.web.reactive.function.client.ExchangeStrategies.withDefaults
import org.springframework.web.util.pattern.PathPatternParser

@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    private val customReactiveOAuth2OidcUserService: CustomReactiveOAuth2OidcUserService,
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun securityWebFilterChain(
            http: ServerHttpSecurity,
            authManager: JwtAuthenticationManager,
            converter: JwtAuthenticationConverter,
            successHandler: OAuth2AuthenticationSuccessHandler,
    ): SecurityWebFilterChain {
        val filter = AuthenticationWebFilter(authManager)
        filter.setServerAuthenticationConverter(converter)

        return http
                .csrf { it.disable() }
                .cors { it.disable() }
                .formLogin { it.disable() }
                .authorizeExchange {
                    exchanges ->
                    exchanges
                            .pathMatchers("/api/user/**").permitAll()
//                            .pathMatchers("/oauth2/**").permitAll()
//                            .pathMatchers("/login/oauth2/**").permitAll()
                            .anyExchange().authenticated()
                }
                .oauth2Login {
                    it.authenticationSuccessHandler(successHandler)
                }
                .oauth2Client {  }
                .addFilterAfter(filter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build()
    }

    @Bean
    fun oidcUserService(): ReactiveOAuth2UserService<OidcUserRequest, OidcUser> {
        return customReactiveOAuth2OidcUserService
    }

    @Bean
    fun corsFilter(): CorsWebFilter {
        val config = CorsConfiguration().apply {
            addAllowedOrigin("*") // 허용할 오리진 설정
            addAllowedHeader("*") // 모든 헤더 허용
            addAllowedMethod("*") // 모든 HTTP 메소드 허용
        }

        val source = UrlBasedCorsConfigurationSource(PathPatternParser()).apply {
            registerCorsConfiguration("/**", config) // 모든 경로에 대해 CORS 설정 적용
        }

        return CorsWebFilter(source)
    }
}