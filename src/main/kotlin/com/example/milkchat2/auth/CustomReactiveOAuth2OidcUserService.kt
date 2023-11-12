package com.example.milkchat2.auth

import com.example.milkchat2.user.CustomOidcUser
import com.example.milkchat2.user.ReactiveUserRepository
import com.example.milkchat2.user.UserRepository
import com.example.milkchat2.user.model.User
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CustomReactiveOAuth2OidcUserService(
        private val reactiveUserRepository: ReactiveUserRepository,
): ReactiveOAuth2UserService<OidcUserRequest, OidcUser> {

    override fun loadUser(userRequest: OidcUserRequest): Mono<OidcUser> {
        println("CustomReactiveOAuth2User")
        val delegate = OidcReactiveOAuth2UserService()
        val clientRegistrationId = userRequest.clientRegistration.registrationId

        val oidcUser: Mono<OidcUser> = delegate.loadUser(userRequest)

        return oidcUser
                .flatMap { oidcUser ->
                    val email = oidcUser.email
                    val nickname = oidcUser.nickName

                    reactiveUserRepository.findByEmail(email)
                            .switchIfEmpty(Mono.defer {
                                val newUser = User(
                                    email = email,
                                    nickname = nickname,
                                    provider = "google",
                                )
                                reactiveUserRepository.save(newUser)
                            })
                            .map { user ->
                                CustomOidcUser(user, oidcUser)
                            }
                }
    }

}
