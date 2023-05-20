package io.bluetape4k.workshop.mongodb.coroutine

import io.bluetape4k.workshop.mongodb.Process
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CoroutineProcessRepository: CoroutineCrudRepository<Process, Int>
