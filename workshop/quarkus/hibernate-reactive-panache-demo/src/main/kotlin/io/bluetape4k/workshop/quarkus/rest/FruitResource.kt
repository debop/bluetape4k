package io.bluetape4k.workshop.quarkus.rest

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.quarkus.model.Fruit
import io.bluetape4k.workshop.quarkus.repository.FruitRepository
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.validation.Valid
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType


@Path("/fruits")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class FruitResource(
    private val repository: FruitRepository,
) {
    companion object: KLogging()

    @GET
    suspend fun getAll(): Flow<Fruit> {
        return repository.listAll().awaitSuspending().asFlow()
    }


    @GET
    @Path("/{name}")
    suspend fun findByName(name: String): Fruit {
        return repository.findByName(name).awaitSuspending()
    }

    /**
     * NOTE: `@ReactiveTransactional` 을 사용하기 위해 suspend 함수보다는 Mutiny 객체를 사용하여 반환합니다.
     * NOTE: `@ReactiveTransactional` 이 suspend 함수를 지원하지 않는다 !!!
     */
    @POST
    @ReactiveTransactional
    fun addFruit(@Valid fruit: Fruit): Uni<Fruit> {
        log.debug { "add fruit. $fruit" }
        return repository.persist(fruit)
    }
}
