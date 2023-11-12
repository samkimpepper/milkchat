package com.example.milkchat2.auth

import com.example.milkchat2.user.UserRepository
import kotlinx.coroutines.reactor.mono
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserDetailsService(
        private val userRepository: UserRepository,
) : ReactiveUserDetailsService {
    override fun findByUsername(username: String?): Mono<UserDetails> {
        return if (username == null) return Mono.empty()
        else {
            mono {
                userRepository.findByEmail(username)?.let { UserDetailsImpl(it) }

            }
        }
    }
}