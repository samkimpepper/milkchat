package com.example.milkchat2.user

import com.example.milkchat2.user.dto.*
import com.example.milkchat2.user.model.CareerStage
import com.example.milkchat2.user.model.JobField
import com.example.milkchat2.user.model.User
import org.springframework.stereotype.Service

interface UserService {
    suspend fun register(registerRequest: RegisterRequest): LoginResponse
    suspend fun login(loginRequest: LoginRequest): LoginResponse
    suspend fun update(user: User, updateRequest: UpdateRequest)
    suspend fun addFavoredUsers(userEmail: String, favoriteUserId: String)
    suspend fun removeFavoredUsers(userEmail: String, favoriteUserId: String)
    suspend fun getFavoredUsers(user: User): List<FavoredUserDto>
    suspend fun updateFcmToken(user: User, fcmToken: String)
}

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val authService: AuthService,
) : UserService {
    override suspend fun register(registerRequest: RegisterRequest): LoginResponse {
        println("register")

        val user = User(
            email = registerRequest.email,
            nickname = registerRequest.nickname,
            password = authService.encodePassword(registerRequest.password),
        ).let {userRepository.save(it)}

        return LoginResponse(user.id, authService.generateToken(user))
    }

    override suspend fun login(loginRequest: LoginRequest): LoginResponse {
        val user = userRepository.findByEmail(loginRequest.email)
        if (authService.isMatchedPassword(loginRequest.password, user.password!!)) {
            return LoginResponse(user.id, authService.generateToken(user))
        } else {
            throw Exception("Wrong password")
        }
    }

    override suspend fun update(user: User, updateRequest: UpdateRequest) {
        updateRequest.careerStage?.let {
            try {
                user.careerStage = enumValueOf<CareerStage>(it)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("틀린 값")
            }
        }

        updateRequest.jobField?.let {
            try {
                user.jobField = enumValueOf<JobField>(it)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("틀린 값")
            }
        }

        userRepository.save(user)
    }

    override suspend fun addFavoredUsers(userEmail: String, favoriteUserId: String) {
        val user = userRepository.findByEmail(userEmail) ?: throw Exception("User not found")
        val favoriteUser = userRepository.findById(favoriteUserId) ?: throw Exception("User not found")

        user.favoredUsers.add(favoriteUser.id!!)
        userRepository.save(user)
    }

    override suspend fun removeFavoredUsers(userEmail: String, favoriteUserId: String) {
        val user = userRepository.findByEmail(userEmail) ?: throw Exception("User not found")
        val favoriteUser = userRepository.findById(favoriteUserId) ?: throw Exception("User not found")

        user.favoredUsers.remove(favoriteUser.id!!)
        userRepository.save(user)
    }

    override suspend fun getFavoredUsers(user: User): List<FavoredUserDto> {
        return user.favoredUsers
                .mapNotNull { id -> userRepository.findById(id) }
                .map { user -> FavoredUserDto(user.nickname!!, user.email!!) }
    }

    override suspend fun updateFcmToken(user: User, fcmToken: String) {
        user.fcmToken = fcmToken
        userRepository.save(user)
    }
}