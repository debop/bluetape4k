package io.bluetape4k.workshop.redis.examples.movie.repository

import io.bluetape4k.workshop.redis.examples.movie.model.Actor
import io.bluetape4k.workshop.redis.examples.movie.model.Movie
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.QueryByExampleExecutor

interface MovieRepository: CrudRepository<Movie, String>, QueryByExampleExecutor<Movie> {

    fun findByYear(year: Int): List<Movie>

    // NOTE: Redis Query는 Equals 와 같이 간단한 쿼리밖에 지원하지 않는다. 
    // fun findByYearAfter(year: Int): List<Movie>

    /**
     * [MovieActor]를 사용해서 [Actor]와 연관된 모든 [Movie]를 찾는다
     *
     * @param actor
     * @return
     */
    fun findByActorsContaining(actor: Actor): List<Movie>
}
