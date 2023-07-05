package io.bluetape4k.openai.clients.retrofit2.utils

class SSEInvalidFormatException: RuntimeException {
    constructor(): super()
    constructor(msg: String): super(msg)
    constructor(msg: String, cause: Throwable?): super(msg, cause)
    constructor(cause: Throwable?): super(cause)
}
