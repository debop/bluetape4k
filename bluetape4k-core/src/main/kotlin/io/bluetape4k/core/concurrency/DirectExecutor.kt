package io.bluetape4k.core.concurrency

import java.util.concurrent.Executor

object DirectExecutor : Executor {
    override fun execute(command: Runnable) {
        command.run()
    }
}
