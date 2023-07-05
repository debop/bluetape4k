package io.bluetape4k.openai.api.exceptions

sealed class OpenAIIOException: OpenAIException {
    constructor(): super()
    constructor(message: String): super(message)
    constructor(message: String?, cause: Throwable?): super(message, cause)
    constructor(cause: Throwable?): super(cause)
}

class OpenAITimeoutException: OpenAIIOException {
    constructor(): super()
    constructor(message: String): super(message)
    constructor(message: String?, cause: Throwable?): super(message, cause)
    constructor(cause: Throwable?): super(cause)
}

class GenericIOException: OpenAIIOException {
    constructor(): super()
    constructor(message: String): super(message)
    constructor(message: String?, cause: Throwable?): super(message, cause)
    constructor(cause: Throwable?): super(cause)
}
