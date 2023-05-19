package io.bluetape4k.workshop.webflux.hibernate.reactive.repository

import io.bluetape4k.data.hibernate.reactive.mutiny.asMutinySessionFactory
import io.bluetape4k.data.hibernate.reactive.mutiny.createQueryAs
import io.bluetape4k.data.hibernate.reactive.mutiny.getAs
import io.bluetape4k.data.hibernate.reactive.mutiny.withStatelessSessionSuspending
import io.bluetape4k.workshop.webflux.hibernate.reactive.model.City
import io.smallrye.mutiny.coroutines.awaitSuspending
import org.hibernate.reactive.mutiny.Mutiny
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import javax.persistence.EntityManagerFactory

@Repository
class CityRespository(@Autowired emf: EntityManagerFactory) {

    val sf: Mutiny.SessionFactory = emf.asMutinySessionFactory()

    suspend fun findById(id: Long): City? {
        return sf.withStatelessSessionSuspending { stateless ->
            stateless.getAs<City>(id).awaitSuspending()
        }
    }

    suspend fun findAll(): List<City> {
        return sf.withStatelessSessionSuspending { stateless ->
            stateless.createQueryAs<City>("from City").resultList.awaitSuspending()
        }
    }
}
