package io.bluetape4k.spring.cassandra.query

import org.springframework.data.cassandra.core.query.Criteria
import org.springframework.data.cassandra.core.query.CriteriaDefinition

infix fun Criteria.eq(value: Any?): CriteriaDefinition = `is`(value)

infix fun Criteria.inValues(values: Collection<Any>): CriteriaDefinition = `in`(values)
fun Criteria.inValues(vararg values: Any): CriteriaDefinition = `in`(*values)
