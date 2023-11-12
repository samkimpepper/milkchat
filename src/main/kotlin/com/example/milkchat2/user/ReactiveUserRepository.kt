package com.example.milkchat2.user

import com.example.milkchat2.user.model.User
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface ReactiveUserRepository: ReactiveMongoRepository<User, String> {
    fun findByEmail(email: String): Mono<User>
}