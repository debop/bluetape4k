package io.bluetape4k.core

import io.bluetape4k.io.serializer.BinarySerializers
import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeEqualTo
import org.junit.jupiter.api.Test
import java.io.Serializable

class ValueWrapperTest {

    companion object: KLogging()

    @Test
    fun `create with null`() {
        val wrapper = ValueWrapper(null)

        wrapper.getOrNull().shouldBeNull()
        wrapper.getOrElse { 42 } shouldBeEqualTo 42
        wrapper.getAs<Int>().shouldBeNull()

        wrapper.toString() shouldBeEqualTo "ValueWrapper(value=null)"
    }

    @Test
    fun `create some value`() {
        val wrapper = ValueWrapper(42)

        wrapper.getOrNull() shouldBeEqualTo 42
        wrapper.getAs<Int>() shouldBeEqualTo 42
        wrapper.toString() shouldBeEqualTo "ValueWrapper(value=42)"

        wrapper shouldBeEqualTo ValueWrapper(42)
        wrapper shouldNotBeEqualTo ValueWrapper(43)
        wrapper shouldNotBeEqualTo ValueWrapper("42")
    }

    data class Data(val value: String): Serializable

    @Test
    fun `jdk serialize with wrapper`() {
        with(BinarySerializers.Jdk) {
            serialize(ValueWrapper(Data("a"))).shouldNotBeEmpty()
            serialize(ValueWrapper(null)).shouldNotBeEmpty()

            deserialize<ValueWrapper>(serialize(ValueWrapper(Data("a")))) shouldBeEqualTo ValueWrapper(Data("a"))
            deserialize<ValueWrapper>(serialize(ValueWrapper(null))) shouldBeEqualTo ValueWrapper(null)
        }
    }

    @Test
    fun `kryo serialize with wrapper`() {
        with(BinarySerializers.Kryo) {
            serialize(ValueWrapper(Data("a"))).shouldNotBeEmpty()
            serialize(ValueWrapper(null)).shouldNotBeEmpty()

            deserialize<ValueWrapper>(serialize(ValueWrapper(Data("a")))) shouldBeEqualTo ValueWrapper(Data("a"))
            deserialize<ValueWrapper>(serialize(ValueWrapper(null))) shouldBeEqualTo ValueWrapper(null)
        }
    }
}
