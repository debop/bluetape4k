package io.bluetape4k.workshop.mongodb.reactive

import io.bluetape4k.workshop.mongodb.Process
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface ReactiveProcessRepository: ReactiveCrudRepository<Process, Int>
