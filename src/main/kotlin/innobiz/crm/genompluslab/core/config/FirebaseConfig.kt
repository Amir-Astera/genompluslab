package com.dev.course.core.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.dev.course.core.config.properties.SecurityProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Primary
import java.io.FileInputStream
import java.io.IOException

@Configuration
class FirebaseConfig(
    private val securityProperties: SecurityProperties
) {
    @Primary
    @Bean
    @Throws(IOException::class)
    fun init(): FirebaseApp {
        val stream = FileInputStream(securityProperties.firebaseProps.googleCredentials)
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(stream)) 
            .build()

        return if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options)
        } else FirebaseApp.initializeApp()
    }

    @Bean
    @DependsOn("init") 
    fun auth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}
