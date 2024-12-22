package innobiz.crm.genompluslab

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication//(scanBasePackages = ["com.dev.course", "com.dev.course.feature"])
@ConfigurationPropertiesScan
@EnableTransactionManagement
@EnableWebFluxSecurity
class GenompluslabApplication

fun main(args: Array<String>) {
	runApplication<GenompluslabApplication>(*args)
}
