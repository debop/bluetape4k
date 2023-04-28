package io.bluetape4k.spring.cassandra.query

import org.springframework.data.cassandra.core.query.Criteria
import org.springframework.data.cassandra.core.query.CriteriaDefinition

infix fun Criteria.eq(value: Any?): CriteriaDefinition = `is`(value)
