package io.bluetape4k.core

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.Test
import kotlin.concurrent.thread

class LocalTest {

    companion object : KLogging()

    @Test
    fun `local value in thread local`() {
        Local["a"] = "Alpha"
        Local["b"] = "Beta"

        Local.get<String>("a") shouldBeEqualTo "Alpha"
        Local.get<String>("b") shouldBeEqualTo "Beta"

        Local.clearAll()
        Local.get<String>("a").shouldBeNull()
        Local.get<String>("b").shouldBeNull()
    }

    @Test
    fun `local value in different thread`() {
        val thread1 = thread {
            Local["a"] = "Alpha"
            Thread.sleep(10)
            Local.get<String>("a") shouldBeEqualTo "Alpha"
        }

        val thread2 = thread {
            Local["a"] = "Beta"
            Thread.sleep(5)
            Local.get<String>("a") shouldBeEqualTo "Beta"
        }

        thread1.join()
        thread2.join()
    }
}
