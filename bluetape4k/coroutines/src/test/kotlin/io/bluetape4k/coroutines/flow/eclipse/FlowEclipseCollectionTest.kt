package io.bluetape4k.coroutines.flow.eclipse

import io.bluetape4k.coroutines.flow.extensions.flowRangeOf
import io.bluetape4k.coroutines.flow.extensions.log
import io.bluetape4k.junit5.coroutines.runSuspendTest
import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.RepeatedTest

class FlowEclipseCollectionTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 3
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `flow to fastList`() = runSuspendTest {
        val flow = flowRangeOf(1, 10).log("flow")
        val list = flow.toFastList()
        list shouldHaveSize 10
        list shouldContainSame flowRangeOf(1, 10).toFastList()
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `flow to unifiedSet`() = runSuspendTest {
        val flow = flowRangeOf(1, 10).log("flow")
        val set = flow.toUnifiedSet()
        set shouldHaveSize 10
        set shouldContainSame flowRangeOf(1, 10).toUnifiedSet()
    }
}
