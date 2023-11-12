package com.example.milkchat2.user

import com.example.milkchat2.user.model.User
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CoroutineCrudRepository<User, String> {
    suspend fun findByEmail(email: String): User

    suspend fun existsByEmail(email: String): Boolean
}