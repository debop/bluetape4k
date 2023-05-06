package io.bluetape4k.core

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeEqualTo
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

class ValueWrapperTest {

    companion object: KLogging()

    @Test
    fun `create with null`() {
        val wrapper = ValueWrapper(null)

        wrapper.getOrNull().shouldBeNull()
        wrapper.getOrElse { 42 } shouldBeEqualTo 42

        wrapper.toString() shouldBeEqualTo "ValueWrapper(value=null)"
    }

    @Test
    fun `create some value`() {
        val wrapper = ValueWrapper(42)

        wrapper.getOrNull() shouldBeEqualTo 42
        wrapper.toString() shouldBeEqualTo "ValueWrapper(value=42)"

        wrapper shouldBeEqualTo ValueWrapper(42)
        wrapper shouldNotBeEqualTo ValueWrapper(43)
        wrapper shouldNotBeEqualTo ValueWrapper("42")
    }

    data class Data(val value: String): Serializable

    @Test
    fun `serialize with wrapper`() {
        serialize(ValueWrapper(Data("a"))).shouldNotBeEmpty()
        serialize(ValueWrapper(null)).shouldNotBeEmpty()


        deserialize(serialize(ValueWrapper(Data("a")))) shouldBeEqualTo ValueWrapper(Data("a"))
        deserialize(serialize(ValueWrapper(null))) shouldBeEqualTo ValueWrapper(null)
    }

    private fun serialize(graph: ValueWrapper): ByteArray {
        return ByteArrayOutputStream().use { bos ->
            ObjectOutputStream(bos).use { oos ->
                oos.writeObject(graph)
                oos.flush()
                bos.toByteArray()
            }
        }
    }

    private fun deserialize(bytes: ByteArray): ValueWrapper? {
        ByteArrayInputStream(bytes).use { bis ->
            ObjectInputStream(bis).use { ois ->
                return ois.readObject() as? ValueWrapper
            }
        }
    }
}
