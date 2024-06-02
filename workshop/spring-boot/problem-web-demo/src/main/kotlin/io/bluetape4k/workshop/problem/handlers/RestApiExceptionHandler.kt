package io.bluetape4k.workshop.problem.handlers

import org.springframework.web.bind.annotation.ControllerAdvice
import org.zalando.problem.spring.webflux.advice.ProblemHandling

@ControllerAdvice
class RestApiExceptionHandler: ProblemHandling, TaskAdviceTrait, Resilience4jTrait {

    override fun isCausalChainsEnabled(): Boolean = true
}
