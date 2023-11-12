package com.example.milkchat2.user

import com.example.milkchat2.SecurityUtils
import com.example.milkchat2.auth.OAuth2TokenService
import com.example.milkchat2.infra.FcmPushClient
import com.example.milkchat2.user.dto.LoginRequest
import com.example.milkchat2.user.dto.RegisterRequest
import com.example.milkchat2.user.dto.UpdateRequest
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.MediaType
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.server.ServerWebExchange

@Component
class UserHandler(
        private val userService: UserService,
        private val userRepository: UserRepository,
        private val fcmPushClient: FcmPushClient,
        private val oAuth2TokenService: OAuth2TokenService,
) {
    suspend fun test(serverRequest: ServerRequest): ServerResponse {
        println("test!!")

        val email = ReactiveSecurityContextHolder.getContext()
                .map{
                    securityContext ->
                    val authentication = securityContext.authentication
                    val principal = authentication.principal
                    principal
                }.awaitSingle()

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValueAndAwait(email)
    }

    suspend fun register(serverRequest: ServerRequest): ServerResponse {
        val registerRequest = serverRequest.awaitBody<RegisterRequest>()

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValueAndAwait(userService.register(registerRequest))
    }

    suspend fun login(serverRequest: ServerRequest): ServerResponse {
        println("login")

        val loginRequest = serverRequest.awaitBody<LoginRequest>()
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValueAndAwait(userService.login(loginRequest))
    }

    suspend fun update(serverRequest: ServerRequest): ServerResponse {
        println("update")

        val updateRequest = serverRequest.awaitBody<UpdateRequest>()
        val email = SecurityUtils.currentUser().awaitFirst()
        val user = userRepository.findByEmail(email)

        userService.update(user, updateRequest)

        return ServerResponse.ok().buildAndAwait()
    }

    suspend fun addFavoredUsers(serverRequest: ServerRequest): ServerResponse {
        println("add")

        val favoriteUserId = serverRequest.pathVariable("id")
        val userEmail = SecurityUtils.currentUser().awaitFirst()

        userService.addFavoredUsers(userEmail, favoriteUserId)
        return ServerResponse.ok().buildAndAwait()
    }

    suspend fun removeFavoredUsers(serverRequest: ServerRequest): ServerResponse {
        println("remove")

        val favoriteUserId = serverRequest.pathVariable("id")
        val userEmail = SecurityUtils.currentUser().awaitFirst()

        userService.removeFavoredUsers(userEmail, favoriteUserId)
        return ServerResponse.ok().buildAndAwait()
    }

    suspend fun getFavoredUsers(serverRequest: ServerRequest): ServerResponse {
        println("get")

        val email = SecurityUtils.currentUser().awaitFirst()
        val user = userRepository.findByEmail(email)

        return ServerResponse.ok().bodyValueAndAwait(userService.getFavoredUsers(user))
    }

    suspend fun updateFcmToken(serverRequest: ServerRequest): ServerResponse {
        println("update fcm")

        val fcmToken = serverRequest.awaitBody<String>()
        val email = SecurityUtils.currentUser().awaitFirst()
        val user = userRepository.findByEmail(email)

        userService.updateFcmToken(user, fcmToken)
        fcmPushClient.sendMessage(user, "알림 성공", "알림 성공이지롱")

        return ServerResponse.ok().buildAndAwait()
    }

    suspend fun getFcmToken(serverRequest: ServerRequest): ServerResponse {
        return ServerResponse.ok()
                .renderAndAwait("firebase-message-sw.js")
    }

    suspend fun getOAuth2Page(serverRequest: ServerRequest): ServerResponse {
        return ServerResponse.ok()
                .renderAndAwait("oauth2-test.html")
    }

    suspend fun oauth2Test(serverRequest: ServerRequest): ServerResponse {
        val email = SecurityUtils.currentUser().awaitFirst()

        val auth = SecurityUtils.curretOAuth2UserInfo().awaitFirst()

        val token = oAuth2TokenService.getAccessToken(serverRequest.exchange()).awaitFirst()
        println("OAuth2 Token: $token")

        return ServerResponse.ok().buildAndAwait()
    }
}