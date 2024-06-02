package io.bluetape4k.workshop.redis.examples.movie.repository

import io.bluetape4k.workshop.redis.examples.movie.model.Actor
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.QueryByExampleExecutor

interface ActorRepository: CrudRepository<Actor, String>, QueryByExampleExecutor<Actor> {

    fun findByFirstname(firstname: String): List<Actor>

    fun findByLastname(lastname: String): List<Actor>

    fun findByFirstnameAndLastname(firstname: String, lastname: String): List<Actor>

}
