package io.bluetape4k.workshop.r2dbc.queryexample

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor

interface PersonRepository: CoroutineCrudRepository<Person, Int>, ReactiveQueryByExampleExecutor<Person> {
}
