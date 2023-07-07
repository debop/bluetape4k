package io.bluetape4k.multibase.exceptions

import io.bluetape4k.exceptions.BluetapeException

open class MultibaseException: BluetapeException {
    constructor(): super()
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable?): super(message, cause)
    constructor(cause: Throwable?): super(cause)
}
