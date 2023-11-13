package com.example.milkchat2.user

import com.example.milkchat2.user.UserHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class UserRouter(private val userHandler: UserHandler) {

    @Bean
    fun userRoutes() = coRouter {
        "/api/user".nest {
            GET("/", userHandler::test)
            POST("/register", userHandler::register)
            POST("/login", userHandler::login)
            PUT("/update", userHandler::update)
            PUT("/favorite/{id}", userHandler::addFavoredUsers)
            DELETE("/favorite/{id}", userHandler::removeFavoredUsers)
            GET("/favorites", userHandler::getFavoredUsers)
            POST("/fcm-token", userHandler::updateFcmToken)
        }
    }

    @Bean
    fun test() = coRouter {
        "/oauth2".nest {
            GET("", userHandler::getOAuth2Page)
            GET("/callback", userHandler::oauth2Test)
        }
    }
}