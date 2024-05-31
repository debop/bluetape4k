package io.bluetape4k.support

val Double.isFinite: Boolean get() = Double.NEGATIVE_INFINITY < this && this < Double.POSITIVE_INFINITY
