package io.bluetape4k.workshop.redis.examples.movie.repository

import io.bluetape4k.workshop.redis.examples.movie.model.MovieActor
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.QueryByExampleExecutor

interface MovieActorRepository: CrudRepository<MovieActor, String>, QueryByExampleExecutor<MovieActor> {

    fun findByMovieHashId(movieHashId: String): List<MovieActor>

    fun findByActorHashId(actorHashId: String): List<MovieActor>
}
