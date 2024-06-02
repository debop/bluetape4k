package io.bluetape4k.workshop.micrometer.config

import io.micrometer.observation.ObservationRegistry
import io.micrometer.observation.aop.ObservedAspect
import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RestController

/**
 * `@Observed` 를 이용하여 메서드 호출을 관찰하는 Aspect 를 활성화합니다.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(ObservedAspect::class)
class ObservationConfig {
    @Bean
    @ConditionalOnBean(ObservationRegistry::class)
    fun observedAspect(observationRegistry: ObservationRegistry): ObservedAspect {
        return ObservedAspect(observationRegistry)
    }

    /**
     * `@RestController` 또는 `@Controller` 를 가진 클래스는 [ObservedAspect]를 적용하지 않도록 할 수 있습니다.
     * ```
     * ObservedAspect(observationRegistry, this::skipControllers)
     * ```
     */
    private fun skipControllers(pjp: ProceedingJoinPoint): Boolean {
        val targetClass: Class<*> = pjp.target.javaClass
        return targetClass.isAnnotationPresent(RestController::class.java) ||
                targetClass.isAnnotationPresent(Controller::class.java)
    }
}
