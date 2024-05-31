package io.bluetape4k.junit5.awaitility

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import org.awaitility.Awaitility
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class AwaitilityConfigurationExtension: BeforeAllCallback {

    companion object: KLogging()

    override fun beforeAll(context: ExtensionContext?) {
        log.trace { "Setup Awaitility configuration ..." }
        Awaitility.catchUncaughtExceptions()
        Awaitility.waitAtMost(5.seconds.toJavaDuration())
        Awaitility.setDefaultPollInterval(10.milliseconds.toJavaDuration())
        Awaitility.setDefaultPollDelay(10.milliseconds.toJavaDuration())
        // Awaitility.pollInSameThread()
    }
}
