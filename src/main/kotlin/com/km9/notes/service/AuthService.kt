package com.km9.notes.service


import com.km9.notes.api.dto.AuthResponse
import com.km9.notes.entity.RefreshToken
import com.km9.notes.entity.User
import com.km9.notes.repository.RefreshTokenRepository
import com.km9.notes.repository.UserRepository
import com.km9.notes.security.HashEncoder
import com.km9.notes.shared.ConflictException
import com.km9.notes.shared.UnauthorizedException
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.time.Instant
import java.util.*

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val hashEncoder: HashEncoder,
    private val refreshTokenRepository: RefreshTokenRepository
) {

    fun register(email: String, password: String): User {
        userRepository.findByEmail(email.trim())?.let {
            throw ConflictException("A user with that email already exists.")
        }

        return userRepository.save(
            User(
                email = email.trim(),
                hashedPassword = hashEncoder.encode(password)
            )
        )
    }

    fun login(email: String, password: String): AuthResponse {
        val user = userRepository.findByEmail(email)
            ?: throw UnauthorizedException("Invalid credentials.")

        if (!hashEncoder.matches(password, user.hashedPassword)) {
            throw UnauthorizedException("Invalid credentials.")
        }

        val newAccessToken = jwtService.generateAccessToken(user.id.toHexString())
        val newRefreshToken = jwtService.generateRefreshToken(user.id.toHexString())

        storeRefreshToken(user.id, newRefreshToken)

        return AuthResponse(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    @Transactional
    fun refresh(refreshToken: String): AuthResponse {
        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw UnauthorizedException("Invalid or expired refresh token.")
        }

        val userId = jwtService.getUserIdFromToken(refreshToken)
        val user = userRepository.findById(ObjectId(userId)).orElseThrow {
            UnauthorizedException("User not found for the provided token.")
        }

        val hashed = hashToken(refreshToken)
        refreshTokenRepository.findByUserIdAndHashedToken(user.id, hashed)
            ?: throw UnauthorizedException("Refresh token not recognized. It may have been used or expired.")

        refreshTokenRepository.deleteByUserIdAndHashedToken(user.id, hashed)

        val newAccessToken = jwtService.generateAccessToken(userId)
        val newRefreshToken = jwtService.generateRefreshToken(userId)

        storeRefreshToken(user.id, newRefreshToken)

        return AuthResponse(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    private fun storeRefreshToken(userId: ObjectId, rawRefreshToken: String) {
        val hashed = hashToken(rawRefreshToken)
        val expiryMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expiryMs)

        refreshTokenRepository.save(
            RefreshToken(
                userId = userId,
                expiresAt = expiresAt,
                hashedToken = hashed
            )
        )
    }

    private fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}