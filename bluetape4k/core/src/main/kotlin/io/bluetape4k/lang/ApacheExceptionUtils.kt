package io.bluetape4k.lang

import org.apache.commons.lang3.exception.ExceptionUtils
import java.io.PrintStream
import java.io.PrintWriter

fun <T: RuntimeException> Throwable.asRuntimeException(): T = ExceptionUtils.asRuntimeException(this)

fun Throwable.forEach(consumer: (Throwable) -> Unit) = ExceptionUtils.forEach(this, consumer)


fun Throwable.getRootCause(): Throwable = ExceptionUtils.getRootCause(this)

fun Throwable.getRootCauseMessage(): String = ExceptionUtils.getRootCauseMessage(this)

fun Throwable.getRootCauseStackTrace(): Array<String> = ExceptionUtils.getRootCauseStackTrace(this)

fun Throwable.getRootCauseStackTraceList(): List<String> = ExceptionUtils.getRootCauseStackTraceList(this)

fun Throwable.getStackFrames(): Array<String> = ExceptionUtils.getStackFrames(this)

fun Throwable.getThrowableCount(): Int = ExceptionUtils.getThrowableCount(this)

fun Throwable.getThrowableList(): List<Throwable> = ExceptionUtils.getThrowableList(this)

fun Throwable.getThrowables(): Array<Throwable> = ExceptionUtils.getThrowables(this)

fun Throwable.hasCause(type: Class<out Throwable>): Boolean = ExceptionUtils.hasCause(this, type)

fun Throwable.printRootCauseStackTrace(printStream: PrintStream = System.err) =
    ExceptionUtils.printRootCauseStackTrace(this, printStream)

fun Throwable.printRootCauseStackTrace(printWriter: PrintWriter) =
    ExceptionUtils.printRootCauseStackTrace(this, printWriter)
