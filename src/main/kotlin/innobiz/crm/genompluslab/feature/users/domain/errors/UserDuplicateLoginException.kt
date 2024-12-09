package com.dev.course.feature.users.domain.errors

class UserDuplicateLoginException : RuntimeException("Duplicate email or phone number. Please try different input.")