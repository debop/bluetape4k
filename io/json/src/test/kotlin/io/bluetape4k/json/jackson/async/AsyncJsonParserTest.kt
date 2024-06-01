package io.bluetape4k.json.jackson.async

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.module.kotlin.treeToValue
import io.bluetape4k.json.jackson.Jackson
import io.bluetape4k.json.jackson.treeToValueOrNull
import io.bluetape4k.json.jackson.writeAsBytes
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.toUtf8String
import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.atomic
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class AsyncJsonParserTest {

    companion object: KLogging()

    data class Model(
        val stringValue: String? = null,
        val intValue: Int? = null,

        val inner: Model? = null,
        val nullable: Double? = null,
        val booleanValue: Boolean = true,
    ) {
        var innerArray: Array<Model>? = null
        var intArray: IntArray? = null
    }

    private val mapper = Jackson.defaultJsonMapper

    private val model = Model(
        stringValue = "안녕하세요",
        intValue = 2,
        inner = Model(
            stringValue = "inner",
        ).apply {
            intArray = intArrayOf(5, 6, 7)
        },
        nullable = null,
        booleanValue = true
    ).apply {
        innerArray = arrayOf(
            Model(stringValue = "innerArray1"),
            Model(stringValue = "innerArray2"),
        )
        intArray = intArrayOf(2, 3, 4)
    }

    @Test
    fun `parse one byte`() {
        val parsed = atomic(0)
        val parser = getSingleModelParser(parsed)

        val bytes = mapper.writeAsBytes(model)!!
        // 1 byte 씩 consume 한다
        bytes.forEach {
            parser.consume(byteArrayOf(it))
        }

        parsed.value shouldBeEqualTo 1
    }

    @Test
    fun `parse chunks`() {
        val parsed = atomic(0)
        val parser = getSingleModelParser(parsed)

        val bytes = mapper.writeAsBytes(model)!!
        val chunkSize = 20
        bytes.toList().chunked(chunkSize).forEach {
            parser.consume(it.toByteArray())
        }

        parsed.value shouldBeEqualTo 1
    }

    @Test
    fun `parse object sequence`() {
        val parsed = atomic(0)
        val parser = getSingleModelParser(parsed)

        val bytes = mapper.writeAsBytes(model)!!
        val repeatSize = 3
        repeat(repeatSize) {
            bytes.forEach {
                parser.consume(byteArrayOf(it))
            }
        }
        parsed.value shouldBeEqualTo repeatSize
    }

    @Test
    fun `parse chunk sequence`() {
        val parsed = atomic(0)
        val parser = getSingleModelParser(parsed)

        val bytes = mapper.writeAsBytes(model)!!
        val repeatSize = 3
        val chunkSize = 20
        repeat(repeatSize) {
            bytes.toList().chunked(chunkSize).forEach {
                log.debug { it.toByteArray().toUtf8String() }
                parser.consume(it.toByteArray())
            }
        }

        parsed.value shouldBeEqualTo repeatSize
    }

    private fun getSingleModelParser(parsed: AtomicInt): AsyncJsonParser {
        return AsyncJsonParser { root ->
            try {
                parsed.incrementAndGet()
                mapper.treeToValueOrNull<Model>(root) shouldBeEqualTo model
            } catch (e: JsonProcessingException) {
                fail(e)
            }
        }
    }


    @Test
    fun `parse array object`() {
        val parsed = atomic(0)
        val modelSize = 3

        val parser = AsyncJsonParser { root ->
            parsed.incrementAndGet()

            try {
                val deserialized = mapper.treeToValue<Array<Model>>(root)
                log.debug { deserialized.contentToString() }
                deserialized shouldHaveSize modelSize
                deserialized shouldBeEqualTo arrayOf(model, model, model)
            } catch (e: JsonProcessingException) {
                fail(e)
            }
        }

        val bytes = mapper.writeAsBytes(model)!!
        parser.consume("[".toByteArray())
        List(modelSize) {
            bytes.forEach {
                parser.consume(byteArrayOf(it))
            }
            if (it != modelSize - 1) {
                parser.consume(",".toByteArray())
            }
        }
        parser.consume("]".toByteArray())

        parsed.value shouldBeEqualTo 1
    }
}
