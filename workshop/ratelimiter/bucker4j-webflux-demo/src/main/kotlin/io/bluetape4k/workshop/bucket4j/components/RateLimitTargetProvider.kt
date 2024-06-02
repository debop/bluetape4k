package io.bluetape4k.workshop.bucket4j.components

import org.springframework.stereotype.Component

@Component
class RateLimitTargetProvider {

    fun getTargets(): List<Regex> {
        return listOf(
            "/api/v1/coroutines/.*".toRegex(),
            "/api/v1/reactive/.*".toRegex()
        )
    }
}
