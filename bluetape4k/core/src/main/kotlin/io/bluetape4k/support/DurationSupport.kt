package io.bluetape4k.support

import java.time.Duration

val Duration.isPositive: Boolean get() = !isZero && !this.isNegative
