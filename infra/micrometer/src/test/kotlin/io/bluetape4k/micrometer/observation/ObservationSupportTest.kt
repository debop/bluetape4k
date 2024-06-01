package io.bluetape4k.micrometer.observation

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import io.micrometer.observation.Observation
import io.micrometer.observation.tck.ObservationContextAssert
import io.micrometer.observation.tck.ObservationRegistryAssert
import org.junit.jupiter.api.Test

class ObservationSupportTest: AbstractObservationTest() {

    companion object: KLogging()

    @Test
    fun `assert observation registry`() {
        ObservationRegistryAssert.assertThat(observationRegistry)
            .doesNotHaveAnyRemainingCurrentObservation()
    }

    @Test
    fun `create Observation`() {
        val observation = Observation.start(faker.name().name(), observationRegistry)
        observation.observe {
            log.info { "observation: ${observation.context.name}" }
        }
    }

    @Test
    fun `withObserver example`() {
        val observationName = "withObserver.method"

        withObservation(observationName, observationRegistry) {
            val observation = observationRegistry.currentObservation!!
            log.info { "observation context: ${observation.context}" }

            ObservationContextAssert.assertThat(observation.context)
                .hasNameEqualTo(observationName)

            ObservationRegistryAssert.assertThat(observationRegistry)
                .hasRemainingCurrentObservationSameAs(observation)

            Thread.sleep(100)
        }

        // withObserver 종료 후에는 current observation이 없어야 한다.
        ObservationRegistryAssert.assertThat(observationRegistry)
            .doesNotHaveAnyRemainingCurrentObservation()

        println(observationRegistry)
    }
}
