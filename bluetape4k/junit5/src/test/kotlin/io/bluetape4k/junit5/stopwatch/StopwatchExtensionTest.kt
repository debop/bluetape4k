package io.bluetape4k.junit5.stopwatch

import io.bluetape4k.logging.KLogging
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@StopwatchTest
class StopWatchExtensionTest {

    companion object : KLogging()

    @Test
    fun `테스트 후 실행시간을 로그애 씁니다`() {
        Thread.sleep(10)
    }

    @StopwatchTest
    fun `메소드 별로 실행 시간을 측정합니다`() {
        Thread.sleep(5)
    }
}
