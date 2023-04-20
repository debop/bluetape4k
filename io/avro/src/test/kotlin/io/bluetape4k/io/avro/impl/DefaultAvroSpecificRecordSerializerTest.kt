package io.bluetape4k.io.avro.impl

import io.bluetape4k.io.avro.AbstractAvroTest
import io.bluetape4k.io.avro.deserialize
import io.bluetape4k.io.avro.message.examples.Employee
import io.bluetape4k.io.avro.message.examples.ProductProperty
import io.bluetape4k.io.avro.message.examples.ProductRoot
import io.bluetape4k.junit5.random.RandomValue
import io.bluetape4k.junit5.random.RandomizedTest
import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.apache.avro.specific.SpecificRecord
import org.junit.jupiter.api.RepeatedTest
import io.bluetape4k.io.avro.message.examples.v1.VersionedItem as ItemV1
import io.bluetape4k.io.avro.message.examples.v2.VersionedItem as ItemV2

@RandomizedTest
class DefaultAvroSpecificRecordSerializerTest: AbstractAvroTest() {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 10
    }

    val serializer = DefaultAvroSpecificRecordSerializer()

    private inline fun <reified T: SpecificRecord> verifySerialization(avroObject: T) {
        val bytes = serializer.serialize(avroObject)!!
        bytes.shouldNotBeEmpty()

        val converted = serializer.deserialize<T>(bytes)
        converted.shouldNotBeNull()
        converted shouldBeEqualTo avroObject
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `serialize single avro object`(@RandomValue employee: Employee) {
        verifySerialization(employee)
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `serialize nested avro object`(
        @RandomValue productRoot: ProductRoot,
        @RandomValue(type = ProductProperty::class) productProps: List<ProductProperty>,
    ) {
        productRoot.productProperties = productProps.toMutableList()
        verifySerialization(productRoot)
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `serialize versioned item v1`(@RandomValue item: ItemV1) {
        verifySerialization(item)
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `serialize versioned item v2`(@RandomValue item: ItemV2) {
        verifySerialization(item)
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `serialize v1 and deserialize as v2`(@RandomValue item: ItemV1) {
        val bytes = serializer.serialize(item)!!

        val convertedAsV2 = serializer.deserialize<ItemV2>(bytes)
        convertedAsV2.shouldNotBeNull()
        convertedAsV2.id shouldBeEqualTo item.id
        convertedAsV2.key shouldBeEqualTo item.key
        convertedAsV2.description.shouldBeNull()
        convertedAsV2.action shouldBeEqualTo "action"  // default value
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `serialize V2 and deserialize as V1`(@RandomValue item: ItemV2) {
        val bytes = serializer.serialize(item)

        val convertedAsV1 = serializer.deserialize<ItemV1>(bytes)
        convertedAsV1.shouldNotBeNull()
        convertedAsV1.id shouldBeEqualTo item.id
        convertedAsV1.key shouldBeEqualTo item.key
    }

}
