package io.bluetape4k.workshop.mongodb.imperative

import io.bluetape4k.workshop.mongodb.Process
import org.springframework.data.repository.CrudRepository

interface ProcessRepository: CrudRepository<Process, Int>
