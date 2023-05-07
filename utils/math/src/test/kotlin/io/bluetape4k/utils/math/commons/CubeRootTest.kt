package io.bluetape4k.utils.math.commons

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import kotlin.math.pow

class CubeRootTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 10
    }

    @Test
    fun `cube root for positive value`() {
        repeat(REPEAT_SIZE) {
            cubeRoot(it.toDouble().pow(3)) shouldBeEqualTo it.toDouble()
        }
    }

    @Test
    fun `cube root for negative value`() {
        repeat(REPEAT_SIZE) {
            cubeRoot(it.unaryMinus().toDouble().pow(3.0)) shouldBeEqualTo it.unaryMinus().toDouble()
        }
    }
}
