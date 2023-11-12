package com.example.milkchat2.user

import com.example.milkchat2.auth.JwtSupport
import com.example.milkchat2.user.model.User
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

interface AuthService {
    fun isMatchedPassword(password: String, encodedPassword: String): Boolean
    fun encodePassword(password: String): String
    suspend fun generateToken(user: User): String
}

@Service
class AuthServiceImpl(
        private val passwordEncoder: PasswordEncoder,
        private val jwtSupport: JwtSupport,
) : AuthService {
    override fun isMatchedPassword(password: String, encodedPassword: String): Boolean {
        return passwordEncoder.matches(password, encodedPassword)
    }

    override fun encodePassword(password: String): String {
        return passwordEncoder.encode(password)
    }

    override suspend fun generateToken(user: User): String {
        val authenticationToken = UsernamePasswordAuthenticationToken(user.email, user.password)

        val securityContext = SecurityContextImpl(authenticationToken)

        return jwtSupport.generate(user.email!!).value
    }
}