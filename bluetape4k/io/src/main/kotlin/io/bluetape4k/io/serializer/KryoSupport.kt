package io.bluetape4k.io.serializer

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer
import com.esotericsoftware.kryo.serializers.EnumNameSerializer
import com.esotericsoftware.kryo.serializers.JavaSerializer
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy
import com.esotericsoftware.kryo.util.Pool
import io.bluetape4k.io.serializer.Kryox.release
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import java.util.concurrent.CompletableFuture
import org.objenesis.strategy.StdInstantiatorStrategy

/**
 * Kryo 를 이용한 작업을 함수로 표현
 * Kryo 가 thread-safe 하지 않기 때문에 이 함수를 사용해야 합니다.
 */
inline fun <T> withKryo(func: Kryo.() -> T): T {
    val kryo = Kryox.obtainKryo()
    return try {
        func(kryo)
    } finally {
        kryo.release()
    }
}

/**
 * Kryo Ouptut을 Pool 에서 받아서 작업 후 반환합니다.
 */
inline fun <T> withKryoOutput(func: (output: Output) -> T): T {
    val output = Kryox.obtainOutput()
    return try {
        func(output)
    } finally {
        output.release()
    }
}

/**
 * Kryo Input을 Pool 에서 받아서 작업 후 반환합니다.
 */
inline fun <T> withKryoInput(func: (input: Input) -> T): T {
    val input = Kryox.obtainInput()
    return try {
        func(input)
    } finally {
        input.release()
    }
}

/**
 * Kryo 를 이용한 비동기 작업을 함수로 표현
 * Kryo 가 thread-safe 하지 않기 때문에 이 함수를 사용해야 합니다.
 */
inline fun <T : Any> withKryoAsync(crossinline func: Kryo.() -> T?): CompletableFuture<T?> {
    val kryo = Kryox.obtainKryo()
    return CompletableFuture.supplyAsync { func(kryo) }
        .whenCompleteAsync { _, _ ->
            kryo.release()
        }
}

object Kryox : KLogging() {

    private val kryoPool by lazy {
        object : Pool<Kryo>(true, false, 1024) {
            override fun create(): Kryo = createKryo()
        }
    }

    private val inputPool by lazy {
        object : Pool<Input>(true, false, 512) {
            override fun create(): Input = Input(DEFAULT_BUFFER_SIZE)
        }
    }

    private val outputPool by lazy {
        object : Pool<Output>(true, false, 512) {
            override fun create(): Output = Output(DEFAULT_BUFFER_SIZE, -1)
        }
    }

    fun createKryo(classLoader: ClassLoader? = null): Kryo {
        log.info { "Create new Kryo instance..." }

        return Kryo().apply {
            classLoader?.let { setClassLoader(classLoader) }

            isRegistrationRequired = false
            references = false
            addDefaultSerializer(Throwable::class.java, JavaSerializer())

            // no-arg constructor 가 없더라도 deserialize 가 가능하도록
            // kryo 5.x
            instantiatorStrategy = DefaultInstantiatorStrategy(StdInstantiatorStrategy())
            // kryo 4.x
            // instantiatorStrategy = Kryo.DefaultInstantiatorStrategy(StdInstantiatorStrategy())

            // schema evolution 시 오류를 줄일 수 있다.
            setDefaultSerializer(CompatibleFieldSerializer::class.java)

            // enum ordinal 이 아닌 name 으로 직렬화
            addDefaultSerializer(Enum::class.java, EnumNameSerializer::class.java)

            register(java.util.Optional::class.java)
        }
    }

    fun obtainKryo(): Kryo = kryoPool.obtain()
    fun obtainInput(): Input = inputPool.obtain()
    fun obtainOutput(): Output = outputPool.obtain()

    fun Kryo.release() {
        kryoPool.free(this)
    }

    fun Input.release() {
        inputPool.free(this)
    }

    fun Output.release() {
        outputPool.free(this)
    }
}
