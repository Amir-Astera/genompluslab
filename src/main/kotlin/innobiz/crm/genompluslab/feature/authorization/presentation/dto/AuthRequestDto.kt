package com.dev.course.feature.authorization.presentation.dto

data class AuthRequestDto(
  val email: String,
  val password: String,
  val returnSecureToken: Boolean
)