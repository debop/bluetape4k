package io.bluetape4k.junit5.awaitility

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import java.time.Duration
import org.awaitility.Awaitility
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

class AwaitilityConfigurationExtension : BeforeAllCallback {

    companion object : KLogging()

    override fun beforeAll(context: ExtensionContext?) {
        log.trace { "Setup Awaitility configuration ..." }
        Awaitility.catchUncaughtExceptions()
        Awaitility.waitAtMost(Duration.ofSeconds(5))
        Awaitility.setDefaultPollInterval(Duration.ofMillis(10))
        Awaitility.setDefaultPollDelay(Duration.ofMillis(10))
        Awaitility.pollInSameThread()
    }
}
