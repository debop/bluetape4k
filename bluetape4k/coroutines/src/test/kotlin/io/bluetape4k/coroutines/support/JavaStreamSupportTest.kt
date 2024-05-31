package io.bluetape4k.coroutines.support

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import java.util.stream.IntStream

class JavaStreamSupportTest {

    @Test
    fun `int stream as flow`() = runTest {
        val list = IntStream.range(1, 10)
            .coMap {
                delay(10)
                it + 1
            }
            .toList()

        list shouldBeEqualTo (2..10).toList()

        val list2 = mutableListOf<Int>()
        IntStream.range(1, 10).coForEach {
            delay(10)
            list2.add(it)
        }
        list2 shouldBeEqualTo List(9) { it + 1 }
    }
}
