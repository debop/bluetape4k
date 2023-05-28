package io.bluetape4k.workshop.r2dbc.queryexample

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor

/**
 * Person repository
 *
 * Query by Example 예제를 위해 [ReactiveQueryByExampleExecutor] 를 상속받습니다.
 */
interface PersonRepository: CoroutineCrudRepository<Person, Int>, ReactiveQueryByExampleExecutor<Person>
