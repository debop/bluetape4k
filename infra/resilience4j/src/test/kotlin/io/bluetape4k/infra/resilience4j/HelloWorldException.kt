package io.bluetape4k.infra.resilience4j

class HelloWorldException: RuntimeException {

    constructor(): super("BAM!")
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable): super(message, cause)
    constructor(cause: Throwable): super(cause)

}
