package io.bluetape4k.io.avro

import io.bluetape4k.collections.eclipse.fastList
import io.bluetape4k.io.avro.message.examples.Employee
import io.bluetape4k.io.avro.message.examples.EmployeeList
import io.bluetape4k.io.avro.message.examples.EventType
import io.bluetape4k.io.avro.message.examples.ProductProperty
import io.bluetape4k.io.avro.message.examples.ProductRoot
import io.bluetape4k.io.avro.message.examples.Suit
import io.bluetape4k.junit5.faker.Fakers

object TestMessageProvider {

    private const val COUNT = 1000

    private val faker = Fakers.faker

    fun createEmployee(): Employee {
        return Employee.newBuilder()
            .setId(faker.random().nextInt())
            .setName(faker.name().fullName())
            .setAddress(faker.address().fullAddress())
            .setAge(faker.random().nextInt(100))
            .setSalary(faker.random().nextLong())
            .setEventType(EventType.CREATED)
            .setHireAt(faker.date().birthday().time)
            .setLastUpdatedAt(faker.date().birthday().time)
            .build()
    }

    fun createEmployeeList(count: Int = COUNT): EmployeeList =
        EmployeeList.newBuilder()
            .setEmps(fastList(count) { createEmployee() })
            .build()


    private fun getValues() = mapOf(
        "name" to faker.name().fullName(),
        "nick" to faker.name().username(),
    )

    fun createProductProperty(id: Long = 1L, valid: Boolean = true): ProductProperty =
        ProductProperty.newBuilder()
            .setId(id)
            .setKey(id.toString())
            .setCreatedAt(faker.date().birthday().time)
            .setUpdatedAt(faker.date().birthday().time)
            .setValid(valid)
            .setValues(getValues())
            .build()

    fun createProductRoot(): ProductRoot =
        ProductRoot.newBuilder()
            .setId(faker.random().nextLong())
            .setCategoryId(faker.random().nextLong())
            .setProductProperties(listOf(createProductProperty(1L), createProductProperty(2L)))
            .setSuit(Suit.HEARTS)
            .build()
}
