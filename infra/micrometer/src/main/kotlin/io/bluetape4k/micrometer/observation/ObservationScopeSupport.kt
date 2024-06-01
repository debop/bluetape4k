package io.bluetape4k.micrometer.observation

import io.micrometer.observation.Observation

inline fun <T> Observation.observe(block: () -> T): T {
    return withObservationContext { _: Observation.Context ->
        block()
    }
}

inline fun <T> Observation.withObservationContext(block: (Observation.Context) -> T): T {
    val self = this
    start()
    return try {
        openScope().use { _ ->
            block(context)
        }
    } catch (e: Throwable) {
        self.error(e)
        throw e
    } finally {
        self.stop()
    }
}
