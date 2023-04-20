package io.bluetape4k.io.avro

import io.bluetape4k.io.avro.message.examples.Employee
import io.bluetape4k.io.avro.message.examples.EmployeeList
import io.bluetape4k.io.avro.message.examples.ProductProperty
import io.bluetape4k.io.avro.message.examples.ProductRoot
import io.bluetape4k.io.avro.message.examples.Suit
import java.time.Instant

object TestMessageProvider {

    const val COUNT = 1000

    fun createEmployee(id: Int): Employee =
        Employee.newBuilder()
            .setId(id)
            .setName("name-$id")
            .setAddress("Seould Rd. $id")
            .setSalary(1000L)
            .setAge(51)
            .setHireAt(Instant.now().toEpochMilli())
            .build()

    fun createEmployeeList(count: Int = COUNT): EmployeeList =
        EmployeeList.newBuilder()
            .setEmps(List(count) { createEmployee(it) })
            .build()


    private val values = mapOf("name" to "Sunghyouk Bae", "nick" to "Debop")

    fun createProductProperty(id: Long = 1L, valid: Boolean = true): ProductProperty =
        ProductProperty.newBuilder()
            .setId(id)
            .setKey(id.toString())
            .setCreatedAt(Instant.now().toEpochMilli())
            .setUpdatedAt(Instant.now().toEpochMilli())
            .setValid(valid)
            .setValues(values)
            .build()

    fun createProductRoot(): ProductRoot =
        ProductRoot.newBuilder()
            .setId(12)
            .setCategoryId(30L)
            .setProductProperties(listOf(createProductProperty(1L), createProductProperty(2L)))
            .setSuit(Suit.HEARTS)
            .build()
}
