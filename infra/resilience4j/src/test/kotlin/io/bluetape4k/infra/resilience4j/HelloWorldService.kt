package io.bluetape4k.infra.resilience4j

import io.github.resilience4j.core.functions.Either
import java.io.IOException
import org.junit.platform.commons.function.Try

interface HelloWorldService {
    fun returnHelloWorld(): String?

    fun returnEither(): Either<HelloWorldException?, String?>?

    fun returnTry(): Try<String?>?

    @Throws(IOException::class)
    fun returnHelloWorldWithException(): String?

    fun returnHelloWorldWithName(name: String?): String?

    @Throws(IOException::class)
    fun returnHelloWorldWithNameWithException(name: String?): String?

    fun sayHelloWorld()

    @Throws(IOException::class)
    fun sayHelloWorldWithException()

    fun sayHelloWorldWithName(name: String?)

    @Throws(IOException::class)
    fun sayHelloWorldWithNameWithException(name: String?)
}
