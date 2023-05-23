package io.bluetape4k.coroutines.support

import io.bluetape4k.collections.eclipse.primitives.intArrayList
import io.bluetape4k.collections.eclipse.primitives.intArrayListOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import java.util.stream.IntStream

class JavaStreamSupportTest {

    @Test
    fun `int stream as flow`() = runTest {
        val list = IntStream.range(1, 10).coMap { it + 1 }.toList()
        list shouldBeEqualTo (2..10).toList()

        val list2 = intArrayListOf()
        IntStream.range(1, 10).coForEach {
            list2.add(it)
        }
        list2 shouldBeEqualTo intArrayList(9) { it + 1 }
    }
}
