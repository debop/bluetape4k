package io.bluetape4k.concurrent

import java.util.concurrent.Executor

object DirectExecutor : Executor {
    override fun execute(command: Runnable) {
        command.run()
    }
}
