package io.bluetape4k.openai.client.sse

class SseFormatException: RuntimeException {
    constructor(): super()
    constructor(msg: String): super(msg)
    constructor(msg: String, cause: Throwable?): super(msg, cause)
    constructor(cause: Throwable?): super(cause)
}
