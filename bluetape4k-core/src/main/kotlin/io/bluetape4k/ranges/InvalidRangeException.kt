package io.bluetape4k.ranges

import io.bluetape4k.exceptions.BluetapeException

open class InvalidRangeException: BluetapeException {
    constructor(): super()
    constructor(msg: String): super(msg)
    constructor(msg: String, cause: Throwable?): super(msg, cause)
    constructor(cause: Throwable?): super(cause)
}
