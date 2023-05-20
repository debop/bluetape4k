package io.bluetape4k.workshop.redis.examples.movie.repository

import io.bluetape4k.workshop.redis.examples.movie.model.Actor
import io.bluetape4k.workshop.redis.examples.movie.model.Movie
import io.bluetape4k.workshop.redis.examples.movie.model.MovieActorReference
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.QueryByExampleExecutor

// NOTE: 검색조건에 @Reference 이 적용된 속성은 지원하지 않습니다. Redis hashId로 검색해야 합니다.
// NOTE: @Reference 속성을 이용한 Example 검색도 지원하지 않습니다.
interface MovieActorReferenceRepository:
    CrudRepository<MovieActorReference, String>,
    QueryByExampleExecutor<MovieActorReference> {

    // NOTE: 검색조건에 @Reference 이 적용된 속성은 지원하지 않습니다. Redis hashId로 검색해야 합니다.
    fun findByActor(actor: Actor): List<MovieActorReference>

    // NOTE: 검색조건에 @Reference 이 적용된 속성은 지원하지 않습니다. Redis hashId로 검색해야 합니다.
    fun findByMovie(movie: Movie): List<MovieActorReference>
}
