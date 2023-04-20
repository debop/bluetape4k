package io.bluetape4k.io.avro.impl

import io.bluetape4k.io.avro.AbstractAvroTest
import io.bluetape4k.io.avro.TestMessageProvider
import io.bluetape4k.io.avro.message.examples.Employee
import io.bluetape4k.io.avro.message.examples.EmployeeList
import io.bluetape4k.io.avro.message.examples.ProductRoot
import io.bluetape4k.junit5.random.RandomValue
import io.bluetape4k.junit5.random.RandomizedTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.apache.avro.generic.GenericData
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.RepeatedTest

@RandomizedTest
class DefaultAvroGenericRecordSerializerTest: AbstractAvroTest() {

    companion object: KLogging() {
        const val REPEAT_SIZE = 10
    }

    val serializer = DefaultAvroGenericRecordSerializer()

    @RepeatedTest(REPEAT_SIZE)
    fun `serialize employee`(@RandomValue emp: Employee) {
        val schema = Employee.getClassSchema()

        val bytes = serializer.serialize(schema, emp)!!
        bytes.shouldNotBeEmpty()

        val record: GenericData.Record = serializer.deserialize(schema, bytes)!!
        log.trace { "record=$record" }
        record.toString() shouldBeEqualTo emp.toString()
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `serialize collections`(@RandomValue(type = Employee::class) emps: List<Employee>) {
        val empList = EmployeeList.newBuilder().setEmps(emps).build()
        val schema = EmployeeList.getClassSchema()

        val bytes = serializer.serialize(schema, empList)!!
        bytes.shouldNotBeEmpty()

        val record: GenericData.Record = serializer.deserialize(schema, bytes)!!
        log.trace { "record=$record" }

        // generic record 는 이렇게 비교할 수 밖에 없다 (수형이 없고, map 형식이므로)
        record.toString() shouldBeEqualTo empList.toString()
    }

    @Disabled("map<string> 에 대해 key를 long type으로 해석합니다. SpecificRecord를 사용하세요")
    @RepeatedTest(REPEAT_SIZE)
    fun `serialize nested entity`() {
        val producct = TestMessageProvider.createProductProperty()
        val schema = ProductRoot.getClassSchema()

        val bytes = serializer.serialize(schema, producct)!!
        bytes.shouldNotBeEmpty()

        val record: GenericData.Record = serializer.deserialize(schema, bytes)!!
        record.shouldNotBeNull()
        log.trace { "record=$record" }
    }
}
