package io.bluetape4k.workshop.es.exception

open class EsDemoException: RuntimeException {

    constructor(): super()
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable?): super(message, cause)
    constructor(cause: Throwable?): super(cause)
}
