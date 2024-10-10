package ru.flounder.dto

import java.time.LocalDateTime

data class TokenResponse(
    val token: String,
    val type: String,
    val id: Int,
    val username: String,
    val email: String,
    val roles: List<String>
)

data class SignupRequestDTO(
    val username: String,
    val email: String,
    val password: String
)

data class StudyModuleInfoResponseDTO(
    val topic: String,
    val language: String,
    val iconPath: String,
    val filePath: String,
    val createdAt: String,
    val authorName: String,
    val displayWords: String
)

data class StudyModuleRequest(
    val author_id: Int,
    val topic: String,
    val language: String,
    val words: List<String>,
    val translations: List<String>,
    val translations_language: String,
    val icon: String
)
