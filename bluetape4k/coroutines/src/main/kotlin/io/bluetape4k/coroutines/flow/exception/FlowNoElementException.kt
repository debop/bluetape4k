package io.bluetape4k.coroutines.flow.exception

open class FlowNoElementException: FlowException {
    constructor(): super()
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable?): super(message, cause)
    constructor(cause: Throwable?): super(cause)
}
