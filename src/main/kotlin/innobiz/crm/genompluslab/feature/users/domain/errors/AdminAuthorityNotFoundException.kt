package com.dev.course.feature.users.domain.errors

class AdminAuthorityNotFoundException : RuntimeException("Admin authority not found. Not enough permissions to execute.")