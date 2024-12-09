package com.dev.course.feature.users.domain.errors

class UserNotFoundException: RuntimeException("User with this ID does not exist. Please try different one.")