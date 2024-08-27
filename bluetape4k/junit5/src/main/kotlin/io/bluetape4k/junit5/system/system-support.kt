package io.bluetape4k.junit5.system

import org.junit.Assume
import java.util.*

fun assumeNotWindows() {
    Assume.assumeFalse(System.getProperty("os.name").lowercase(Locale.getDefault()).contains("win"))
}
