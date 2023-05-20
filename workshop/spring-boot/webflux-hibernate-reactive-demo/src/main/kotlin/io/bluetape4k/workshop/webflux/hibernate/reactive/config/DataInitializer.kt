package io.bluetape4k.workshop.webflux.hibernate.reactive.config

import io.bluetape4k.data.hibernate.reactive.mutiny.asMutinySessionFactory
import io.bluetape4k.data.hibernate.reactive.mutiny.createQueryAs
import io.bluetape4k.data.hibernate.reactive.mutiny.withSessionSuspending
import io.bluetape4k.data.hibernate.reactive.mutiny.withTransactionSuspending
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.info
import io.bluetape4k.workshop.webflux.hibernate.reactive.model.City
import io.bluetape4k.workshop.webflux.hibernate.reactive.model.Customer
import io.smallrye.mutiny.coroutines.awaitSuspending
import kotlinx.coroutines.runBlocking
import org.hibernate.reactive.mutiny.Mutiny
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import javax.persistence.EntityManagerFactory

@Component
class DataInitializer(
    private val emf: EntityManagerFactory,
): ApplicationRunner {

    companion object: KLogging()

    override fun run(args: ApplicationArguments?) {
        runBlocking {

            log.info { "Initialize sample data..." }

            emf.asMutinySessionFactory().withTransactionSuspending { session: Mutiny.Session ->
                val seoul = City("Seoul")
                val london = City("London")
                val newyork = City("NewYork")

                val debop = Customer("Debop").apply { city = seoul }
                val kim = Customer("Kim").apply { city = seoul }
                val smith = Customer("Smith").apply { city = london }
                val bob = Customer("Bob").apply { city = newyork }

                session.persistAll(seoul, london, newyork).awaitSuspending()
                session.persistAll(debop, kim, smith, bob).awaitSuspending()
            }

            val customers = emf.asMutinySessionFactory()
                .withSessionSuspending { session: Mutiny.Session ->
                    session.enableFetchProfile("withCity")
                        .createQueryAs<Customer>("from Customer")
                        .resultList
                        .awaitSuspending()
                }
            log.debug { "Saved customer." }
            customers.forEach { log.debug { it } }
        }
    }
}
