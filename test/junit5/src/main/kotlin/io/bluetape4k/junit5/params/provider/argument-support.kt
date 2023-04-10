package io.bluetape4k.junit5.params.provider

import org.junit.jupiter.params.provider.Arguments

fun argumentOf(vararg arguments: Any?): Arguments = Arguments.of(*arguments)
