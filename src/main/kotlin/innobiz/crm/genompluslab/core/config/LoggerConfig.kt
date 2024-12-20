package innobiz.crm.genompluslab.core.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InjectionPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class LoggerConfig {
	@Bean
	fun logger(injectionPoint: InjectionPoint): Logger? {
		return LoggerFactory.getLogger(injectionPoint.methodParameter!!.containingClass)
	}
}