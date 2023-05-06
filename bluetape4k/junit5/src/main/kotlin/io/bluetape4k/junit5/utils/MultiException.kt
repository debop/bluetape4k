package io.bluetape4k.junit5.utils

import org.junit.runner.notification.RunListener.ThreadSafe
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Allows multiple exceptions to be thrown as a single exception -- adapted from Jetty.
 *
 * @constructor Create empty Multi exception
 */
@ThreadSafe
class MultiException: RuntimeException("Multiple exceptions") {

    companion object {
        private const val serialVersionUID = 1L
        private const val EXCEPTION_SEPARATOR =
            "\n\t______________________________________________________________________\n"
    }

    private val nested = mutableListOf<Throwable>()

    /**
     * 예외 정보를 추가합니다.
     *
     * @param throwable 추가할 예외 정보. null 이면 추가하지 않습니다.
     */
    fun add(throwable: Throwable?) {
        if (throwable != null) {
            synchronized(nested) {
                if (throwable is MultiException) {
                    val otherNested = throwable.nested
                    synchronized(otherNested) {
                        nested.addAll(otherNested)
                    }
                } else {
                    nested.add(throwable)
                }
            }
        }
    }

    fun isEmpty() {
        synchronized(nested) {
            nested.isEmpty()
        }
    }

    /**
     * 추가된 예외가 없으면, 아무런 동작을 하지 않습니다.
     * 추가된 예외가 하나라면 [Throwable]을 던집니다.
     * 추가된 예외가 볷수개라면 [MultiException]을 던집니다.
     */
    fun throwIfNotEmpty() {
        synchronized(nested) {
            when {
                nested.size == 1 -> throw nested[0]
                nested.size > 1  -> throw this
                else             -> { /* do nothing */
                }
            }
        }
    }

    override val message: String by lazy { buildMessage() }

    private fun buildMessage(): String {
        return synchronized(nested) {
            if (nested.isEmpty()) {
                "<no nested exceptions>"
            } else {
                buildString {
                    val n = nested.size
                    append(n).append(if (n == 1) " nested exception:" else " nested exceptions:")
                    nested.forEach { t ->
                        appendLine(EXCEPTION_SEPARATOR)
                        StringWriter().use { sw ->
                            PrintWriter(sw).use { pw ->
                                t.printStackTrace(pw)
                            }
                            append(sw.toString().replace("\n", "\n\t").trim())
                        }
                    }
                    appendLine(EXCEPTION_SEPARATOR)
                }
            }
        }
    }
}
