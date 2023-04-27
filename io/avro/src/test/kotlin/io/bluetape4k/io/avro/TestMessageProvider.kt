package io.bluetape4k.io.avro

import io.bluetape4k.io.avro.message.examples.*
import net.datafaker.Faker

object TestMessageProvider {

    private const val COUNT = 1000

    private val faker = Faker()

    fun createEmployee(): Employee {
        return Employee.newBuilder()
            .setId(AbstractAvroTest.faker.random().nextInt())
            .setName(AbstractAvroTest.faker.name().fullName())
            .setAddress(AbstractAvroTest.faker.address().fullAddress())
            .setAge(AbstractAvroTest.faker.random().nextInt(100))
            .setSalary(AbstractAvroTest.faker.random().nextLong())
            .setEventType(EventType.CREATED)
            .setHireAt(AbstractAvroTest.faker.date().birthday().time)
            .setLastUpdatedAt(AbstractAvroTest.faker.date().birthday().time)
            .build()
    }

    fun createEmployeeList(count: Int = COUNT): EmployeeList =
        EmployeeList.newBuilder()
            .setEmps(List(count) { createEmployee() })
            .build()


    private val values = mapOf(
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
            .setValues(values)
            .build()

    fun createProductRoot(): ProductRoot =
        ProductRoot.newBuilder()
            .setId(faker.random().nextLong())
            .setCategoryId(faker.random().nextLong())
            .setProductProperties(listOf(createProductProperty(1L), createProductProperty(2L)))
            .setSuit(Suit.HEARTS)
            .build()
}
