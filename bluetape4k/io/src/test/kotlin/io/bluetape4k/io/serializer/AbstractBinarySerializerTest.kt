package io.bluetape4k.io.serializer

import io.bluetape4k.core.AbstractValueObject
import io.bluetape4k.io.compressor.BZip2Compressor
import io.bluetape4k.io.compressor.Compressor
import io.bluetape4k.io.compressor.DeflateCompressor
import io.bluetape4k.io.compressor.GZipCompressor
import io.bluetape4k.io.compressor.LZ4Compressor
import io.bluetape4k.io.compressor.SnappyCompressor
import io.bluetape4k.io.compressor.XZCompressor
import io.bluetape4k.io.compressor.ZstdCompressor
import io.bluetape4k.junit5.random.RandomValue
import io.bluetape4k.junit5.random.RandomizedTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import net.datafaker.Faker
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.Serializable
import java.util.*
import java.util.stream.Stream

@RandomizedTest
abstract class AbstractBinarySerializerTest {

    companion object: KLogging() {
        const val REPEAT_SIZE = 10

        val faker = Faker()
    }

    abstract val serializer: BinarySerializer

    data class SimpleData(
        val id: Long,
        val name: String,
        val age: Int,
        val birth: Date,
        val biography: String,
        val zip: String,
        val address: String,
    ): Serializable


    @RepeatedTest(REPEAT_SIZE)
    fun `serialize primitive type`(@RandomValue expected: Long) {

        val bytes = serializer.serialize(expected)
        bytes.shouldNotBeEmpty()

        val actual = serializer.deserialize<Long>(bytes)
        actual.shouldNotBeNull()
        actual shouldBeEqualTo expected
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `serialize primitive array`(@RandomValue(type = Long::class) numbers: List<Long>) {
        val expected = numbers.toLongArray()

        val bytes = serializer.serialize(expected)
        bytes.shouldNotBeEmpty()

        val actual = serializer.deserialize<LongArray>(bytes)
        actual.shouldNotBeNull()
        actual shouldBeEqualTo expected
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `serialize data class`(@RandomValue expected: SimpleData) {
        val bytes = serializer.serialize(expected)
        bytes.shouldNotBeEmpty()

        val actual: SimpleData? = serializer.deserialize(bytes)
        actual.shouldNotBeNull()
        actual shouldBeEqualTo expected
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `serialize collections`(@RandomValue(type = SimpleData::class, size = 500) expected: List<SimpleData>) {
        val bytes = serializer.serialize(expected)
        bytes.shouldNotBeEmpty()

        val actual: List<SimpleData>? = serializer.deserialize(bytes)
        actual.shouldNotBeNull()
        actual.size shouldBeEqualTo expected.size
        actual shouldContainSame expected
    }


    private val compressorList = listOf(
        BZip2Compressor(),
        DeflateCompressor(),
        GZipCompressor(),
        LZ4Compressor(),
        SnappyCompressor(),
        XZCompressor(),
        ZstdCompressor(),
    )

    private fun getCompressors(): Stream<out Compressor> = compressorList.stream()

    @ParameterizedTest(name = "serialize and compress data - {0}")
    @MethodSource("getCompressors")
    fun `serialize and compress data`(compressor: Compressor, @RandomValue origin: SimpleData) {
        val serde = CompressableBinarySerializer(serializer, compressor)

        val compressed = serde.serialize(origin)
        val actual = serde.deserialize<SimpleData>(compressed)

        actual.shouldNotBeNull()
        actual shouldBeEqualTo origin
    }

    @ParameterizedTest(name = "serialize and compress collection - {0}")
    @MethodSource("getCompressors")
    fun `serialize and compress collection`(
        compressor: Compressor,
        @RandomValue(type = SimpleData::class, size = 500) origins: List<SimpleData>,
    ) {
        val serde = CompressableBinarySerializer(serializer, compressor)

        val compressed = serde.serialize(origins)
        val actual = serde.deserialize<List<SimpleData>>(compressed)

        actual.shouldNotBeNull()
        actual shouldContainSame origins
    }

    //
    // Schema Evolution
    //

    open class PersonV1(
        open val name: String,
        open val email: String,
        open val age: Int,
    ): AbstractValueObject() {
        override fun equalProperties(other: Any): Boolean =
            other is PersonV1 && name == other.name && email == other.email && age == other.age
    }

    open class PersonV2(
        override val name: String,
        override val email: String,
        override val age: Int,
        open val ssn: String = "123",
    ): PersonV1(name, email, age) {
        override fun equalProperties(other: Any): Boolean =
            other is PersonV2 && name == other.name && email == other.email && age == other.age
    }

    @Test
    fun `serialize person v1`() {
        val expected = PersonV1(
            faker.name().name(),
            faker.internet().emailAddress(),
            faker.random().nextInt(15, 99),
        )
        val actual = serializer.deserialize<PersonV1>(serializer.serialize(expected))
        actual shouldBeEqualTo expected
    }

    @Test
    fun `serialize person v2`() {
        val expected = PersonV2(
            faker.name().name(),
            faker.internet().emailAddress(),
            faker.random().nextInt(15, 99),
            faker.idNumber().ssnValid()
        )
        val actual = serializer.deserialize<PersonV2>(serializer.serialize(expected))
        actual shouldBeEqualTo expected
    }

    @Test
    fun `serialize person V1 then deserialize as person V2`() {
        val expected = PersonV1(
            faker.name().name(),
            faker.internet().emailAddress(),
            faker.random().nextInt(15, 99),
        )
        val actual = serializer.deserialize<Any>(serializer.serialize(expected))

        actual.shouldNotBeNull()
        log.debug { "actual class = ${actual.javaClass}" }
        actual shouldBeEqualTo expected
    }

    @Test
    fun `serialize person V2 then deserialize as person V1`() {
        val expected = PersonV2(
            faker.name().name(),
            faker.internet().emailAddress(),
            faker.random().nextInt(15, 99),
            faker.idNumber().ssnValid()
        )
        val actual = serializer.deserialize<PersonV1>(serializer.serialize(expected))
        actual shouldBeEqualTo expected
    }
}
