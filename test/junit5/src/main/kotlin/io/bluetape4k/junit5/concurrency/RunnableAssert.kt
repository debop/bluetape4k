package io.bluetape4k.junit5.concurrency

abstract class RunnableAssert(val description: String) {

    abstract fun run()

    override fun toString(): String {
        return "RunnableAssert($description)"
    }
}
