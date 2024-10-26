package io.bluetape4k.workshop.webflux.hibernate.reactive.repository

import io.bluetape4k.hibernate.reactive.mutiny.asMutinySessionFactory
import io.bluetape4k.hibernate.reactive.mutiny.createQueryAs
import io.bluetape4k.hibernate.reactive.mutiny.getAs
import io.bluetape4k.hibernate.reactive.mutiny.withStatelessSessionSuspending
import io.bluetape4k.hibernate.reactive.mutiny.withTransactionSuspending
import io.bluetape4k.workshop.webflux.hibernate.reactive.model.City
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.persistence.EntityManagerFactory
import org.hibernate.reactive.mutiny.Mutiny
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class CityRepository(@Autowired emf: EntityManagerFactory) {

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

    suspend fun <T> persist(entity: T): T {
        return sf.withTransactionSuspending { session ->
            session.persist(entity).replaceWith(entity).awaitSuspending()
        }
    }
}
