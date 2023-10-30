package io.bluetape4k.workshop.es.domain.exception

import io.bluetape4k.workshop.es.exception.EsDemoException

class BookNotFoundException: EsDemoException {
    constructor(): super()
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable?): super(message, cause)
    constructor(cause: Throwable?): super(cause)
}
